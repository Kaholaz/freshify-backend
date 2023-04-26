package no.freshify.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.CreateUser;
import no.freshify.api.model.dto.LoginUser;
import no.freshify.api.model.dto.UpdateUser;
import no.freshify.api.security.AuthenticationService;
import no.freshify.api.security.UserAuthentication;
import no.freshify.api.security.UserDetailsImpl;
import no.freshify.api.service.HouseholdService;
import no.freshify.api.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private HouseholdService householdService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsImpl userDetails;

    private User user;
    private Authentication authentication;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setEmail("test");
        user.setFirstName("test");
        user.setPassword("test");

        userDetails = new UserDetailsImpl(user.getId(), user.getEmail(), user.getFirstName(), user.getPassword(), Collections.emptyList());

        authentication = new UserAuthentication(userDetails);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testCreateUser_Success() throws Exception {
        when(userService.getUserByEmail(anyString())).thenReturn(null);
        when(userService.createUser(any(User.class))).thenReturn(user);

        CreateUser createUser = new CreateUser();
        createUser.setEmail(user.getEmail());
        createUser.setFirstName(user.getFirstName());
        createUser.setPassword(user.getPassword());

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()));

        verify(userService, times(1)).getUserByEmail(anyString());
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    public void testCreateUser_Conflict() throws Exception {
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        CreateUser createUser = new CreateUser();
        createUser.setEmail(user.getEmail());
        createUser.setFirstName(user.getFirstName());
        createUser.setPassword(user.getPassword());

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUser)))
                .andExpect(status().isConflict())
                .andExpect(content().string("User already exists"));

        verify(userService, times(1)).getUserByEmail(user.getEmail());
        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    public void testGetUserByEmail_Success() throws Exception {
        when(userService.getUserByEmail(anyString())).thenReturn(user);

        mockMvc.perform(get("/user")
                        .param("email", user.getEmail()))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUserByEmail(user.getEmail());
    }

    @Test
    public void testGetUserByEmail_UserNotFoundException() throws Exception {
        when(userService.getUserByEmail(user.getEmail())).thenReturn(null);

        mockMvc.perform(get("/user")
                        .param("email", user.getEmail()))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserByEmail(user.getEmail());
    }

    @Test
    public void testLogout_Success() throws Exception {
        mockMvc.perform(post("/user/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out"))
                .andExpect(cookie().maxAge("Authorization", 0));
    }

    @Test
    public void testUpdateUser_Success() throws Exception {
        UpdateUser updateUser = new UpdateUser();
        updateUser.setId(user.getId());
        updateUser.setEmail("new");
        updateUser.setFirstName("new");
        updateUser.setPassword("new");

        when(userService.getUserById(anyLong())).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");

        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(updateUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(updateUser.getFirstName()));

        verify(userService, times(1)).getUserById(anyLong());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userService, times(1)).updateUser(any(User.class));
    }

    @Test
    public void testLogin_Success() throws Exception {
        LoginUser loginUser = new LoginUser();
        loginUser.setEmail(user.getEmail());
        loginUser.setPassword(user.getPassword());

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        when(authenticationService.generateToken(any(Authentication.class))).thenReturn("jwt");
        when(userService.getUserByEmail(anyString())).thenReturn(user);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()));

        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
        verify(authenticationService, times(1)).generateToken(any(Authentication.class));
        verify(userService, times(1)).getUserByEmail(anyString());
    }

    @Test
    public void testLogin_Unauthorized() throws Exception {
        LoginUser loginUser = new LoginUser();
        loginUser.setEmail(user.getEmail());
        loginUser.setPassword("wrong pass");

        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(new BadCredentialsException("Incorrect credentials"));

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Incorrect user credentials"));

        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
        verify(authenticationService, never()).generateToken(any(Authentication.class));
        verify(userService, never()).getUserByEmail(anyString());
    }

    @Test
    public void testGetUserById_Success() throws Exception {
        when(userService.getUserById(user.getId())).thenReturn(user);

        mockMvc.perform(get("/user/" + user.getId())
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()));

        verify(userService, times(1)).getUserById(user.getId());
    }


    @Test
    public void testGetHouseholds_Success() throws Exception {
        when(householdService.getHouseholds(user.getId())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/" + user.getId() + "/households")
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(householdService, times(1)).getHouseholds(user.getId());
    }

    @Test
    public void testDeleteUser_Success() throws Exception {
        when(userService.deleteUser(user.getId())).thenReturn(user);

        mockMvc.perform(delete("/user/" + user.getId())
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted"));

        verify(userService, times(1)).deleteUser(user.getId());
    }

}