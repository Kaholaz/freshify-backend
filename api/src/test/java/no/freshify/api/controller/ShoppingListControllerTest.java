package no.freshify.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.*;
import no.freshify.api.model.mapper.ShoppingListEntryMapper;
import no.freshify.api.repository.HouseholdRepository;
import no.freshify.api.repository.ShoppingListEntryRepository;
import no.freshify.api.repository.UserRepository;
import no.freshify.api.security.AuthenticationService;
import no.freshify.api.security.UserAuthentication;
import no.freshify.api.security.UserDetailsImpl;
import no.freshify.api.service.HouseholdService;
import no.freshify.api.service.ItemTypeService;
import no.freshify.api.service.ShoppingListEntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;

@WebMvcTest(ShoppingListController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ShoppingListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    ShoppingListEntryService shoppingListEntryService;

    @MockBean
    ShoppingListEntryRepository shoppingListEntryRepository;

    @MockBean
    HouseholdRepository householdRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    AuthenticationService authenticationService;

    @MockBean
    HouseholdService householdService;

    @MockBean
    ShoppingListEntryMapper shoppingListEntryMapper;

    @MockBean
    ItemTypeService itemTypeService;

    @MockBean
    private UserDetailsImpl userDetails;

    private User user;
    private Authentication authentication;
    private Long householdId = 1L;
    private Household household;
    private HouseholdDTO householdDTO;
    private List<UserFull> users;
    private ItemType itemType;
    private ShoppingListEntry shoppingListEntry;
    private ShoppingListEntryRequest shoppingListEntryRequest;
    private ShoppingListEntryResponse shoppingListEntryResponse;
    private ShoppingListEntryEditRequest shoppingListEntryEditRequest;

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

        shoppingListEntryRequest = new ShoppingListEntryRequest();
        shoppingListEntryRequest.setItemTypeId(1L);
        shoppingListEntryRequest.setCount(2L);
        shoppingListEntryRequest.setSuggested(false);

        itemType = new ItemType();
        itemType.setId(1L);
        itemType.setName("Test Item Type");

        shoppingListEntryResponse = new ShoppingListEntryResponse();
        shoppingListEntryResponse.setId(1L);
        shoppingListEntryResponse.setType(itemType);
        shoppingListEntryResponse.setChecked(false);
        shoppingListEntryResponse.setSuggested(false);
        shoppingListEntryResponse.setCount(2L);

        shoppingListEntry = new ShoppingListEntry();
        shoppingListEntry.setId(1L);
        shoppingListEntry.setType(itemType);
        shoppingListEntry.setChecked(false);
        shoppingListEntry.setSuggested(false);
        shoppingListEntry.setCount(2L);
        shoppingListEntry.setAddedBy(user);
        shoppingListEntry.setHousehold(household);

        shoppingListEntryEditRequest = new ShoppingListEntryEditRequest();
        shoppingListEntryEditRequest.setId(1L);
        shoppingListEntryEditRequest.setCount(3L);
        shoppingListEntryEditRequest.setChecked(true);
        shoppingListEntryEditRequest.setSuggested(true);
    }

    @Test
    public void testAddItem() throws Exception {
        when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(household);
        when(itemTypeService.getItemTypeById(anyLong())).thenReturn(itemType);
        when(shoppingListEntryMapper.toShoppingListEntryResponse(any(ShoppingListEntry.class)))
                .thenReturn(shoppingListEntryResponse);
        when(shoppingListEntryService.findShoppingListEntryByItemType(anyLong(), anyLong()))
                .thenReturn(shoppingListEntry);

        doNothing().when(shoppingListEntryService).addItem(shoppingListEntry);

        mockMvc.perform(post("/household/1/shoppinglist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shoppingListEntryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.type.id").value(shoppingListEntryRequest.getItemTypeId()))
                .andExpect(jsonPath("$.count").value(shoppingListEntryRequest.getCount()))
                .andExpect(jsonPath("$.suggested").value(shoppingListEntryRequest.getSuggested()))
                .andExpect(jsonPath("$.checked").value(false));

        verify(householdService, VerificationModeFactory.times(1)).findHouseholdByHouseholdId(anyLong());
        verify(authenticationService, VerificationModeFactory.times(1)).getLoggedInUser();
        verify(itemTypeService, VerificationModeFactory.times(1)).getItemTypeById(anyLong());
        verify(shoppingListEntryService, VerificationModeFactory.times(1))
                .findShoppingListEntryByItemType(anyLong(), anyLong());
    }

    @Test
    public void testUpdateShoppingListEntry() throws Exception {
        //when(authenticationService.getLoggedInUser()).thenReturn(user);
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(household);
        when(itemTypeService.getItemTypeById(anyLong())).thenReturn(itemType);
        when(shoppingListEntryService.findShoppingListEntryByItemType(anyLong(), anyLong()))
                .thenReturn(shoppingListEntry);
        doNothing().when(shoppingListEntryService).updateShoppingListEntry(any(ShoppingListEntry.class));

        mockMvc.perform(put("/household/1/shoppinglist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shoppingListEntryEditRequest)))
                .andExpect(status().isOk());

        verify(householdService, VerificationModeFactory.times(1)).findHouseholdByHouseholdId(anyLong());
        // verify(authenticationService, VerificationModeFactory.times(1)).getLoggedInUser();
        verify(itemTypeService, VerificationModeFactory.times(1)).getItemTypeById(anyLong());
        verify(shoppingListEntryService, VerificationModeFactory.times(1))
                .findShoppingListEntryByItemType(anyLong(), anyLong());
        verify(shoppingListEntryService, VerificationModeFactory.times(1))
                .updateShoppingListEntry(any(ShoppingListEntry.class));
    }
}
