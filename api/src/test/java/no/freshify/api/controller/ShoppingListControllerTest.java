package no.freshify.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.HouseholdDTO;
import no.freshify.api.model.dto.ShoppingListEntryEditRequest;
import no.freshify.api.model.dto.ShoppingListEntryRequest;
import no.freshify.api.model.dto.UserFull;
import no.freshify.api.repository.HouseholdRepository;
import no.freshify.api.repository.ShoppingListEntryRepository;
import no.freshify.api.security.UserAuthentication;
import no.freshify.api.security.UserDetailsImpl;
import no.freshify.api.service.ShoppingListEntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;

@WebMvcTest(ShoppingListController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ShoppingListControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    ShoppingListEntryService shoppingListEntryService;

    @MockBean
    private UserDetailsImpl userDetails;

    private User user;
    private Authentication authentication;
    private Long householdId = 1L;
    private Household household;
    private HouseholdDTO householdDTO;
    private List<UserFull> users;

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
    public void testAddItem() throws Exception {
        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(householdService.addHousehold(any(Household.class))).thenReturn(household);

        ShoppingListEntryRequest request = new ShoppingListEntryRequest();
        request.setItemTypeId(1L);
        request.setCount(2L);
        request.setSuggested(false);

        mockMvc.perform(post("/household/1/shoppinglist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.type.id").value(request.getItemTypeId()))
                .andExpect(jsonPath("$.count").value(request.getCount()))
                .andExpect(jsonPath("$.suggested").value(request.getSuggested()));
    }

    @Test
    public void testUpdateShoppingListEntry() throws Exception {
        ShoppingListEntryEditRequest requestBody = new ShoppingListEntryEditRequest();
        requestBody.setId(1L);
        requestBody.setCount(2L);
        requestBody.setChecked(true);
        requestBody.setSuggested(false);

        mockMvc.perform(put("/household/1/shoppinglist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(requestBody.getCount()))
                .andExpect(jsonPath("$.checked").value(requestBody.getChecked()))
                .andExpect(jsonPath("$.suggested").value(requestBody.getSuggested()))
                .andExpect(jsonPath("$.type.id").value(requestBody.getId()));
    }

    @Test
    public void testGetShoppingList() throws Exception {
        /**
        ShoppingListEntry entry1 = new ShoppingListEntry();
        entry1.setHousehold(householdRepository.findHouseholdById(1L));

        ShoppingListEntry entry2 = new ShoppingListEntry();
        entry2.setHousehold(householdRepository.findHouseholdById(1L));
         */

        ShoppingListEntryRequest entry1 = new ShoppingListEntryRequest();
        entry1.setItemTypeId(1L);
        entry1.setCount(2L);
        entry1.setSuggested(false);

        ShoppingListEntryRequest entry2 = new ShoppingListEntryRequest();
        entry2.setItemTypeId(2L);
        entry2.setCount(3L);
        entry2.setSuggested(true);
        ShoppingListEntry shoppingListEntry = new ShoppingListEntry();

        //TODO add 2 test items to shopping list
        mockMvc.perform(post("/household/1/shoppinglist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/household/1/shoppinglist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/household/1/shoppinglist")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].type.id", is(1)))
                .andExpect(jsonPath("$[0].count", is(2)))
                .andExpect(jsonPath("$[0].suggested", is(false)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].type.id", is(2)))
                .andExpect(jsonPath("$[1].count", is(3)))
                .andExpect(jsonPath("$[1].suggested", is(true)));
    }
}
