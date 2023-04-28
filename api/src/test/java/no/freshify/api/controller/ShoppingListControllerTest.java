package no.freshify.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.*;
import no.freshify.api.model.mapper.ShoppingListEntryMapper;
import no.freshify.api.model.mapper.UserMapper;
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
import org.mapstruct.factory.Mappers;
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
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShoppingListController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ShoppingListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    UserMapper userMapper = Mappers.getMapper(UserMapper.class);

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
    private UserFull userFull;
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
    private List<ShoppingListEntry> shoppingList;

    @BeforeEach
    public void setup() {
        // Setup user with auth
        user = new User();
        user.setId(1L);
        user.setEmail("test@");

        userFull = userMapper.toUserFull(user);

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

        userDetails = new UserDetailsImpl(user.getId(), user.getEmail(), "password", user.getPassword(),
                List.of(householdMember));
        authentication = new UserAuthentication(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

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
        shoppingListEntryResponse.setAddedBy(userFull);

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

        shoppingList = new ArrayList<>(List.of(shoppingListEntry));
    }

    @Test
    public void testAddItem() throws Exception {
        when(authenticationService.isSuperuser(anyLong())).thenReturn(true);
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(household);
        when(shoppingListEntryMapper.toShoppingListEntryResponse(any(ShoppingListEntry.class)))
                .thenReturn(shoppingListEntryResponse);
        when(shoppingListEntryService.addItem(any(ShoppingListEntry.class))).thenReturn(shoppingListEntry);


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
    }

    @Test
    public void testDeleteShoppingListEntry() throws Exception {
        doNothing().when(shoppingListEntryService).deleteShoppingListEntryById(anyLong(), anyLong());
        when(shoppingListEntryService.getShoppingList(anyLong()))
                .thenReturn(shoppingList);
        shoppingListEntry.setSuggested(false);
        when(shoppingListEntryService.findShoppingListEntryById(anyLong())).thenReturn(shoppingListEntry);
        when(authenticationService.isSuperuser(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/household/1/shoppinglist/1")
                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNoContent());

        verify(shoppingListEntryService, VerificationModeFactory.times(1))
                .deleteShoppingListEntryById(anyLong(), anyLong());
    }

    @Test
    public void testGetShoppingList() throws Exception {
        when(shoppingListEntryService.getShoppingList(anyLong()))
                .thenReturn(shoppingList);
        when(shoppingListEntryMapper.toShoppingListEntryResponse(anyList()))
                .thenReturn(List.of(shoppingListEntryResponse));

        mockMvc.perform(get("/household/1/shoppinglist")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(shoppingListEntryResponse.getId()))
                .andExpect(jsonPath("$.[0].checked").value(shoppingListEntryResponse.getChecked()))
                .andExpect(jsonPath("$.[0].count").value(shoppingListEntryResponse.getCount()))
                .andExpect(jsonPath("$.[0].suggested").value(shoppingListEntryResponse.getSuggested()))
                .andExpect(jsonPath("$.[0].type.id").value(shoppingListEntryResponse.getType().getId()))
                .andExpect(jsonPath("$.[0].addedBy.id").value(shoppingListEntryResponse.getAddedBy().getId()));

        verify(shoppingListEntryService, VerificationModeFactory.times(1))
                .getShoppingList(anyLong());
    }

    @Test
    public void testUpdateShoppingListEntry() throws Exception {
        when(shoppingListEntryService.findShoppingListEntryById(any(Long.class))).thenReturn(shoppingListEntry);
        doNothing().when(shoppingListEntryService).updateShoppingListEntry(any(ShoppingListEntry.class));

        mockMvc.perform(put("/household/1/shoppinglist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shoppingListEntryEditRequest)))
                .andExpect(status().isOk());

        verify(shoppingListEntryService, VerificationModeFactory.times(1))
                .updateShoppingListEntry(any(ShoppingListEntry.class));
    }
}
