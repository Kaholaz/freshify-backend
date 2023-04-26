package no.freshify.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.CreateHousehold;
import no.freshify.api.model.dto.HouseholdDTO;
import no.freshify.api.model.dto.UserFull;
import no.freshify.api.model.mapper.HouseholdMapper;
import no.freshify.api.security.AuthenticationService;
import no.freshify.api.security.UserAuthentication;
import no.freshify.api.security.UserDetailsImpl;
import no.freshify.api.service.HouseholdMemberService;
import no.freshify.api.service.HouseholdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;

@WebMvcTest(HouseholdController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class HouseholdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    HouseholdService householdService;

    @MockBean
    HouseholdMemberService householdMemberService;

    @MockBean
    AuthenticationService authenticationService;

    @MockBean
    private HouseholdMapper householdMapper;

    @MockBean
    private UserDetailsImpl userDetails;

    private User user;
    private Authentication authentication;
    private Long householdId = 1L;
    private Household household;
    private HouseholdDTO householdDTO;
    private List<UserFull> users;
    private List<HouseholdMember> members;

    @BeforeEach
    public void setup() {
        // Setup user with auth
        user = new User();
        user.setId(1L);
        user.setEmail("test@");

        userDetails = new UserDetailsImpl(user.getId(), user.getEmail(), "password", user.getPassword(), Collections.emptyList());
        authentication = new UserAuthentication(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Setup household
        household = new Household();
        household.setId(householdId);
        household.setName("Test Household");

        householdDTO  = new HouseholdDTO();
        householdDTO.setId(householdId);
        householdDTO.setName("Test Household");

        HouseholdMember householdMember = new HouseholdMember();
        householdMember.setId(new HouseholdMemberKey(household.getId(), user.getId()));
        householdMember.setHousehold(household);
        householdMember.setRole(HouseholdMemberRole.SUPERUSER);
        householdMember.setUser(user);

        members = new ArrayList<>();
        members.add(householdMember);
        Set<HouseholdMember> householdMembers = new HashSet<>(Collections.singletonList(householdMember));

        household.setHouseholdMembers(householdMembers);
        user.setHouseholdMembers(householdMembers);

        // Setup users
        users = new ArrayList<>();
        users.addAll(List.of(
                new UserFull(1L, "user1", "user1@"),
                new UserFull(2L, "user2", "user2@")
        ));
    }

    @Test
    public void createHousehold() throws Exception {
        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(householdService.addHousehold(any(Household.class))).thenReturn(household);

        CreateHousehold createHousehold = new CreateHousehold();
        createHousehold.setName("Test Household");

        mockMvc.perform(post("/household")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createHousehold)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test Household")))
                .andReturn();

        verify(householdService, VerificationModeFactory.times(1)).addHousehold(Mockito.any(Household.class));
        verify(authenticationService, VerificationModeFactory.times(1)).getLoggedInUser();
    }

    @Test
    public void deleteHouseholdTest() throws Exception {
        when(householdService.findHouseholdByHouseholdId(householdId)).thenReturn(household);
        when(householdService.removeHousehold(anyLong())).thenReturn(ResponseEntity.noContent().build());
        mockMvc.perform(delete("/household/{id}", householdId))
                .andExpect(status().isNoContent());

        verify(householdService, VerificationModeFactory.times(1)).removeHousehold(anyLong());
        verify(householdService, VerificationModeFactory.times(1)).findHouseholdByHouseholdId(Mockito.any());
    }

    @Test
    public void getHouseholdByIdTest() throws Exception {
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(household);
        when(householdMapper.toHouseholdDTO(any(Household.class))).thenReturn(householdDTO);

        mockMvc.perform(get("/household/{id}", householdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(householdId.intValue())))
                .andExpect(jsonPath("$.name", is(household.getName())));

        verify(householdService, VerificationModeFactory.times(1)).findHouseholdByHouseholdId(anyLong());
    }

    @Test
    public void getUsersTest() throws Exception {
        when(householdService.getUsers(anyLong())).thenReturn(members);
        mockMvc.perform(get("/household/{id}/users", householdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(householdService, VerificationModeFactory.times(1)).getUsers(anyLong());
    }

    @Test
    public void updateHouseholdTest() throws Exception {
        HouseholdDTO household = new HouseholdDTO();
        household.setName("New Household Name");
        household.setId(1L);

        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(this.household);
        when(householdService.updateHousehold(any(Household.class))).thenReturn(this.household);

        mockMvc.perform(put("/household/{id}", householdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(household)))
                        .andExpect(status().isOk());

        verify(householdService, VerificationModeFactory.times(1)).findHouseholdByHouseholdId(anyLong());
        verify(householdService, VerificationModeFactory.times(1)).updateHousehold(any(Household.class));
    }
}
