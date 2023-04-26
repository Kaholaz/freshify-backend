package no.freshify.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.freshify.api.exception.HouseholdMemberAlreadyExistsException;
import no.freshify.api.exception.HouseholdNotFoundException;
import no.freshify.api.exception.UserNotFoundException;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.UserTypeRequest;
import no.freshify.api.service.HouseholdMemberService;
import no.freshify.api.service.HouseholdService;
import no.freshify.api.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HouseholdMemberController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class HouseholdMemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HouseholdMemberService householdMemberService;

    @MockBean
    private HouseholdService householdService;

    @MockBean
    private UserService userService;

    private Household household;
    private User user;
    private HouseholdMember householdMember;
    private UserTypeRequest userTypeRequest;

    @BeforeEach
    public void setup() {
        household = new Household();
        household.setId(1L);
        household.setName("Test");

        user = new User();
        user.setId(1L);
        user.setEmail("test@");

        householdMember = new HouseholdMember();
        householdMember.setUser(user);
        householdMember.setHousehold(household);
        householdMember.setRole(HouseholdMemberRole.USER);
        householdMember.setId(new HouseholdMemberKey(household.getId(), user.getId()));

        userTypeRequest = new UserTypeRequest();
        userTypeRequest.setUserId(1L);
        userTypeRequest.setUserType("SUPERUSER");
    }

    @Test
    public void testAddUser_Success() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(household);
        when(householdMemberService.addHouseholdMember(any(HouseholdMember.class))).thenReturn(householdMember);

        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("userId", 1L);

        mockMvc.perform(post("/household/1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().string("Operation successful"));

        verify(userService, times(1)).getUserById(anyLong());
        verify(householdService, times(1)).findHouseholdByHouseholdId(anyLong());
        verify(householdMemberService, times(1)).addHouseholdMember(any(HouseholdMember.class));
    }

    @Test
    public void testUpdateHouseholdMemberRole_Success() throws Exception {
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(household);
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(householdMemberService.getHouseholdMemberByHouseholdMemberKey(any(HouseholdMemberKey.class))).thenReturn(householdMember);
        when(householdMemberService.updateHouseholdMember(any(HouseholdMember.class))).thenReturn(householdMember);

        mockMvc.perform(put("/household/1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userTypeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(user.getId()))
                .andExpect(jsonPath("$.id.householdId").value(household.getId()))
                .andExpect(jsonPath("$.role").value(householdMember.getRole().toString()));

        verify(householdService, times(1)).findHouseholdByHouseholdId(anyLong());
        verify(userService, times(1)).getUserById(anyLong());
        verify(householdMemberService, times(1)).getHouseholdMemberByHouseholdMemberKey(any(HouseholdMemberKey.class));
        verify(householdMemberService, times(1)).updateHouseholdMember(any(HouseholdMember.class));
    }

    @Test
    public void testRemoveUserFromHousehold_Success() throws Exception {
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(household);
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(householdMemberService.getHouseholdMemberByHouseholdMemberKey(any(HouseholdMemberKey.class))).thenReturn(householdMember);

        mockMvc.perform(delete("/household/1/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Operation successful"));

        verify(householdService, times(1)).findHouseholdByHouseholdId(anyLong());
        verify(userService, times(1)).getUserById(anyLong());
        verify(householdMemberService, times(1)).getHouseholdMemberByHouseholdMemberKey(any(HouseholdMemberKey.class));
        verify(householdMemberService, times(1)).removeHouseholdMember(any(HouseholdMember.class));
    }

    @Test
    public void testAddUser_UserNotFound() throws Exception {
        when(userService.getUserById(anyLong())).thenThrow(new UserNotFoundException());

        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("userId", 1L);

        mockMvc.perform(post("/household/1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(userService, times(1)).getUserById(anyLong());
        verify(householdService, times(0)).findHouseholdByHouseholdId(anyLong());
        verify(householdMemberService, times(0)).addHouseholdMember(any(HouseholdMember.class));
    }

    @Test
    public void testAddUser_HouseholdNotFound() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenThrow(new HouseholdNotFoundException());

        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("userId", 1L);

        mockMvc.perform(post("/household/1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Household not found"));

        verify(userService, times(1)).getUserById(anyLong());
        verify(householdService, times(1)).findHouseholdByHouseholdId(anyLong());
        verify(householdMemberService, times(0)).addHouseholdMember(any(HouseholdMember.class));
    }

    @Test
    public void testAddUser_HouseholdMemberAlreadyExists() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(household);
        when(householdMemberService.addHouseholdMember(any(HouseholdMember.class))).thenThrow(new HouseholdMemberAlreadyExistsException());

        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("userId", 1L);

        mockMvc.perform(post("/household/1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Household member already exists"));

        verify(userService, times(1)).getUserById(anyLong());
        verify(householdService, times(1)).findHouseholdByHouseholdId(anyLong());
        verify(householdMemberService, times(1)).addHouseholdMember(any(HouseholdMember.class));
    }

    @Test
    public void testUpdateHouseholdMemberRole_HouseholdNotFound() throws Exception {
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenThrow(new HouseholdNotFoundException());

        mockMvc.perform(put("/household/1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userTypeRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Household not found"));

        verify(householdService, times(1)).findHouseholdByHouseholdId(anyLong());
        verify(userService, times(0)).getUserById(anyLong());
        verify(householdMemberService, times(0)).getHouseholdMemberByHouseholdMemberKey(any(HouseholdMemberKey.class));
        verify(householdMemberService, times(0)).updateHouseholdMember(any(HouseholdMember.class));
    }

    @Test
    public void testUpdateHouseholdMemberRole_UserNotFound() throws Exception {
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(household);
        when(userService.getUserById(anyLong())).thenThrow(new UserNotFoundException());

        mockMvc.perform(put("/household/1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userTypeRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(householdService, times(1)).findHouseholdByHouseholdId(anyLong());
        verify(userService, times(1)).getUserById(anyLong());
        verify(householdMemberService, times(0)).getHouseholdMemberByHouseholdMemberKey(any(HouseholdMemberKey.class));
        verify(householdMemberService, times(0)).updateHouseholdMember(any(HouseholdMember.class));
    }

    @Test
    public void testUpdateHouseholdMemberRole_InvalidHouseholdMemberRole() throws Exception {
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(household);
        when(userService.getUserById(anyLong())).thenReturn(user);

        HouseholdMemberKey householdMemberKey = new HouseholdMemberKey(household.getId() , user.getId());
        when(householdMemberService.getHouseholdMemberByHouseholdMemberKey(householdMemberKey)).thenReturn(householdMember);

        userTypeRequest.setUserType("INVALIDROLE");

        mockMvc.perform(put("/household/1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userTypeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid household member role"));

        verify(householdService , times(1)).findHouseholdByHouseholdId(anyLong());
        verify(userService , times(1)).getUserById(anyLong());
        verify(householdMemberService , times(1)).getHouseholdMemberByHouseholdMemberKey(any(HouseholdMemberKey.class));
    }
}
