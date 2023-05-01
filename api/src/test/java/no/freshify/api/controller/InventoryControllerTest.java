package no.freshify.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.freshify.api.exception.IllegalItemParameterException;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.WastedItemDTO;
import no.freshify.api.model.mapper.ItemMapper;
import no.freshify.api.model.mapper.ItemMapperImpl;
import no.freshify.api.security.UserAuthentication;
import no.freshify.api.security.UserDetailsImpl;
import no.freshify.api.service.HouseholdService;
import no.freshify.api.service.ItemService;
import no.freshify.api.service.ItemTypeService;
import no.freshify.api.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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


@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class InventoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final ItemMapper itemMapper = new ItemMapperImpl();

    @MockBean
    private HouseholdService householdService;

    @MockBean
    private UserService userService;

    @MockBean
    private ItemTypeService itemTypeService;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserDetailsImpl userDetails;

    private Household household;
    private User user;
    private ItemType itemType;
    private Item item;
    private List<Item> wastedItems;
    private List<WastedItemDTO> wastedItemDTOS;
    private Date startDate;
    private Date lastChanged;
    private Map<String, Object> requestBody;
    private List<Map<String, Object>> requestBodyList;

    private Authentication authentication;

    @BeforeEach
    public void setup() throws IllegalItemParameterException {
        startDate = new java.sql.Date(System.currentTimeMillis());
        lastChanged = new java.sql.Date(System.currentTimeMillis() + 10L);

        household = new Household();
        household.setId(1L);
        household.setName("Test Household");

        user = new User();
        user.setId(1L);
        user.setEmail("test@");

        itemType = new ItemType();
        itemType.setId(1L);
        itemType.setName("Test Item Type");

        item = new Item();
        item.setId(1L);
        item.setType(itemType);
        item.setStatus(ItemStatus.INVENTORY);
        item.setHousehold(household);
        item.setAddedBy(user);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setType(itemType);
        item2.setHousehold(household);
        item2.setRemaining(0.6D);
        item2.setStatus(ItemStatus.USED);
        item2.setLastChanged(lastChanged);

        Item item3 = new Item();
        item3.setId(3L);
        item3.setType(itemType);
        item3.setHousehold(household);
        item3.setRemaining(0.1D);
        item3.setStatus(ItemStatus.USED);
        item3.setLastChanged(lastChanged);

        wastedItems = List.of(item2, item3);
        wastedItemDTOS = new ArrayList<>();
        wastedItemDTOS.add(new WastedItemDTO(itemMapper.toItemTypeDTO(item2.getType()), item2.getRemaining()));
        wastedItemDTOS.add(new WastedItemDTO(itemMapper.toItemTypeDTO(item3.getType()), item3.getRemaining()));

        userDetails = new UserDetailsImpl(user.getId(), user.getEmail(), "password", user.getPassword(), Collections.emptyList());

        requestBody = new HashMap<>();
        requestBody.put("itemTypeId", itemType.getId());
        requestBody.put("count", 1);

        requestBodyList = new ArrayList<>();
        requestBodyList.add(requestBody);

        authentication = new UserAuthentication(userDetails);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testAddInventoryItem_Success() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(household);
        when(itemTypeService.getItemTypeById(anyLong())).thenReturn(itemType);
        when(itemService.addItem(any(Item.class))).thenReturn(item);

        mockMvc.perform(post("/household/1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBodyList)))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.[0].type.id").value(itemType.getId()))
                .andExpect(jsonPath("$.[0].status").value(item.getStatus().toString()))
                .andExpect(jsonPath("$.[0].addedBy.id").value(user.getId()));

        verify(userService, times(1)).getUserById(anyLong());
        verify(householdService, times(1)).findHouseholdByHouseholdId(anyLong());
        verify(itemTypeService, times(1)).getItemTypeById(anyLong());
        verify(itemService, times(1)).addItem(any(Item.class));
    }

    @Test
    public void testGetInventoryItems_Success() throws Exception {
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(household);
        when(itemService.getInventoryItems(any(Household.class))).thenReturn(Collections.singletonList(item));

        mockMvc.perform(get("/household/1/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(item.getId()))
                .andExpect(jsonPath("$.[0].type.id").value(itemType.getId()))
                .andExpect(jsonPath("$.[0].status").value(item.getStatus().toString()))
                .andExpect(jsonPath("$.[0].addedBy.id").value(user.getId()));

        verify(householdService, times(1)).findHouseholdByHouseholdId(anyLong());
        verify(itemService, times(1)).getInventoryItems(any(Household.class));
    }

    @Test
    public void testDeleteInventoryItem_Success() throws Exception {
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(household);
        when(itemService.getItemByIdAndHousehold(anyLong(), any(Household.class))).thenReturn(item);

        mockMvc.perform(delete("/household/1/inventory/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted inventory item with id: 1"));

        verify(householdService, times(1)).findHouseholdByHouseholdId(anyLong());
        verify(itemService, times(1)).getItemByIdAndHousehold(anyLong(), any(Household.class));
        verify(itemService, times(1)).deleteItemById(anyLong());
    }

    @Test
    public void testUpdateInventoryItem_Success() throws Exception {
        requestBody.put("itemId", item.getId());
        requestBody.put("remaining", 0.1);
        requestBody.put("state", ItemStatus.USED.toString());

        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(household);
        when(itemService.getItemByIdAndHousehold(anyLong(), any(Household.class))).thenReturn(item);
        when(itemService.updateItem(any(Item.class))).thenReturn(item);

        mockMvc.perform(put("/household/1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated inventory item with id: 1"));

        verify(householdService, times(1)).findHouseholdByHouseholdId(anyLong());
        verify(itemService, times(1)).getItemByIdAndHousehold(anyLong(), any(Household.class));
        verify(itemService, times(1)).updateItem(any(Item.class));
    }

    @Test
    public void testAddInventorySuggestion_Success() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(household);
        when(itemTypeService.getItemTypeById(anyLong())).thenReturn(itemType);
        when(itemService.addItem(any(Item.class))).thenReturn(item);

        mockMvc.perform(post("/household/1/inventory/suggest")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBodyList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].type.id").value(itemType.getId()))
                .andExpect(jsonPath("$.[0].status").value(item.getStatus().toString()))
                .andExpect(jsonPath("$.[0].suggested").value(true))
                .andExpect(jsonPath("$.[0].addedBy.id").value(user.getId()));

        verify(userService, times(1)).getUserById(anyLong());
        verify(householdService, times(1)).findHouseholdByHouseholdId(anyLong());
        verify(itemTypeService, times(1)).getItemTypeById(anyLong());
        verify(itemService, times(1)).addItem(any(Item.class));
    }

    @Test
    public void testGetSortedInventoryWaste_Success() throws Exception {
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(household);
        when(itemService.findWastedItemsInTimeInterval
                (any(Household.class), any(Date.class), any(Date.class)))
                .thenReturn(wastedItems);
        when(itemService.getSortedItemsByWaste(anyList(), any(ItemSortMethod.class)))
                .thenReturn(wastedItemDTOS);

        mockMvc.perform(get("/household/1/inventory/waste?limit=10&start_date=2000-1-1&end_date=2040-1-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wastedItemsByCount.[0].itemType.id").value(wastedItems.get(1).getType().getId()))
                .andExpect(jsonPath("$.wastedItemsByAverageAmount.[0].itemType.id").value(wastedItems.get(1).getType().getId()));

        verify(householdService, times(1)).findHouseholdByHouseholdId(anyLong());
        verify(itemService, times(1)).findWastedItemsInTimeInterval
                (any(Household.class), any(Date.class), any(Date.class));
        verify(itemService, times(2)).getSortedItemsByWaste(anyList(), any(ItemSortMethod.class));
    }
}