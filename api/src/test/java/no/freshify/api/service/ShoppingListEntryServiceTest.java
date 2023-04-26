package no.freshify.api.service;

import no.freshify.api.exception.HouseholdNotFoundException;
import no.freshify.api.exception.InvalidItemCountException;
import no.freshify.api.exception.ShoppingListEntryAlreadyExistsException;
import no.freshify.api.exception.ShoppingListEntryNotFoundException;
import no.freshify.api.model.Household;
import no.freshify.api.model.ItemType;
import no.freshify.api.model.ShoppingListEntry;
import no.freshify.api.repository.HouseholdRepository;
import no.freshify.api.repository.ShoppingListEntryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingListEntryServiceTest {
    @Mock
    private ShoppingListEntryRepository shoppingListEntryRepository;

    @Mock
    private HouseholdRepository householdRepository;

    @Mock
    private HouseholdService householdService;

    @InjectMocks
    private ShoppingListEntryService shoppingListEntryService;

    private ShoppingListEntry shoppingListEntry;
    private Household household;
    private ItemType itemType;

    private final Long householdId = 1L;
    private final Long itemTypeId = 2L;
    private final Long listEntryId = 3L;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        household = new Household();
        household.setId(householdId);

        itemType = new ItemType();
        itemType.setId(itemTypeId);

        shoppingListEntry = new ShoppingListEntry();
        shoppingListEntry.setId(listEntryId);
        shoppingListEntry.setHousehold(household);
        shoppingListEntry.setType(itemType);
    }

    @Test
    public void testAddItem() throws ShoppingListEntryAlreadyExistsException {
        Mockito.when(shoppingListEntryRepository.existsById(listEntryId)).thenReturn(false);

        shoppingListEntryService.addItem(shoppingListEntry);

        Mockito.verify(shoppingListEntryRepository, Mockito.times(1)).save(shoppingListEntry);
    }

    @Test
    public void testAddItemThrowsException() {
        Mockito.when(shoppingListEntryRepository.existsById(listEntryId)).thenReturn(true);

        assertThrows(ShoppingListEntryAlreadyExistsException.class, () -> {
            shoppingListEntryService.addItem(shoppingListEntry);
        });
    }

    @Test
    public void testUpdateShoppingListEntry() throws InvalidItemCountException, ShoppingListEntryNotFoundException {
        Mockito.when(shoppingListEntryRepository.findById(shoppingListEntry.getId())).thenReturn(Optional.of(shoppingListEntry));
        shoppingListEntry.setCount(2L);
        shoppingListEntryService.updateShoppingListEntry(shoppingListEntry);

        Mockito.verify(shoppingListEntryRepository, Mockito.times(1)).save(shoppingListEntry);
    }

    @Test
    public void testUpdateShoppingListEntryThrowsException() {
        shoppingListEntry.setCount((long) -1);

        assertThrows(InvalidItemCountException.class, () -> {
            shoppingListEntryService.updateShoppingListEntry(shoppingListEntry);
        });

        Mockito.verify(shoppingListEntryRepository, Mockito.never()).save(shoppingListEntry);
    }

    @Test
    public void testFindShoppingListEntryByItemType() throws HouseholdNotFoundException, ShoppingListEntryNotFoundException {
        List<ShoppingListEntry> shoppingListEntries = Collections.singletonList(shoppingListEntry);

        Mockito.when(householdService.householdExists(householdId)).thenReturn(true);

        Mockito.when(shoppingListEntryRepository.findByHouseholdId(householdId)).thenReturn(shoppingListEntries);

        ShoppingListEntry result = shoppingListEntryService.findShoppingListEntryByItemType(householdId, itemTypeId);

        assertEquals(shoppingListEntry, result);
    }

    @Test
    public void testFindShoppingListEntryByItemTypeThrowsHouseholdNotFoundException() {
        Mockito.when(householdRepository.existsById(householdId)).thenReturn(false);

        assertThrows(HouseholdNotFoundException.class, () -> {
            shoppingListEntryService.findShoppingListEntryByItemType(householdId, itemTypeId);
        });

        Mockito.verify(shoppingListEntryRepository, Mockito.never()).findByHouseholdId(householdId);
    }

    @Test
    public void testFindShoppingListEntryByItemTypeThrowsShoppingListEntryNotFoundException() {
        Mockito.when(shoppingListEntryRepository.findByHouseholdId(householdId)).thenReturn(Collections.emptyList());
        Mockito.when(householdService.householdExists(householdId)).thenReturn(true);

        assertThrows(ShoppingListEntryNotFoundException.class, () -> {
            shoppingListEntryService.findShoppingListEntryByItemType(householdId, itemTypeId);
        });
    }

    @Test
    public void testGetShoppingList() throws HouseholdNotFoundException {
        List<ShoppingListEntry> shoppingListEntries = Collections.singletonList(shoppingListEntry);

        Mockito.when(householdService.householdExists(householdId)).thenReturn(true);
        Mockito.when(shoppingListEntryRepository.findByHouseholdId(householdId)).thenReturn(shoppingListEntries);

        List<ShoppingListEntry> result = shoppingListEntryService.getShoppingList(householdId);

        assertEquals(shoppingListEntries, result);
    }

    @Test
    public void testGetShoppingListThrowsHouseholdNotFoundException() {
        Mockito.when(householdRepository.existsById(householdId)).thenReturn(false);

        assertThrows(HouseholdNotFoundException.class, () -> {
            shoppingListEntryService.getShoppingList(householdId);
        });

        Mockito.verify(shoppingListEntryRepository, Mockito.never()).findByHouseholdId(householdId);
    }

    @Test
    public void testFindShoppingListEntryById() throws ShoppingListEntryNotFoundException {
        Mockito.when(shoppingListEntryRepository.findById(listEntryId)).thenReturn(Optional.of(shoppingListEntry));

        ShoppingListEntry result = shoppingListEntryService.findShoppingListEntryById(listEntryId);

        assertEquals(shoppingListEntry, result);
    }

    @Test
    public void testFindShoppingListEntryByIdThrowsShoppingListEntryNotFoundException() {
        Mockito.when(shoppingListEntryRepository.findById(listEntryId)).thenReturn(Optional.empty());

        assertThrows(ShoppingListEntryNotFoundException.class, () -> {
            shoppingListEntryService.findShoppingListEntryById(listEntryId);
        });
    }

    @Test
    public void testDeleteShoppingListEntryById() throws ShoppingListEntryNotFoundException, HouseholdNotFoundException {
        List<ShoppingListEntry> shoppingListEntries = Collections.singletonList(shoppingListEntry);

        Mockito.when(householdService.householdExists(householdId)).thenReturn(true);
        Mockito.when(shoppingListEntryRepository.findByHouseholdId(householdId)).thenReturn(shoppingListEntries);
        Mockito.when(shoppingListEntryRepository.findById(listEntryId)).thenReturn(Optional.of(shoppingListEntry));

        shoppingListEntryService.deleteShoppingListEntryById(householdId, listEntryId);

        Mockito.verify(shoppingListEntryRepository, Mockito.times(1)).deleteById(listEntryId);
    }

    @Test
    public void testDeleteShoppingListEntryByIdThrowsShoppingListEntryNotFoundException() throws HouseholdNotFoundException {

        Mockito.when(shoppingListEntryService.getHouseholdService().householdExists(householdId)).thenReturn(true);
        Mockito.when(shoppingListEntryService.getShoppingList(householdId)).thenReturn(new ArrayList<>());

        assertThrows(ShoppingListEntryNotFoundException.class, () -> {
            shoppingListEntryService.deleteShoppingListEntryById(householdId, listEntryId);
        });

        Mockito.verify(shoppingListEntryRepository, Mockito.never()).deleteById(listEntryId);
    }

    @Test
    public void testDeleteShoppingListEntryByIdThrowsHouseholdNotFoundException() {
        Mockito.when(householdRepository.existsById(householdId)).thenReturn(false);

        assertThrows(HouseholdNotFoundException.class, () -> {
            shoppingListEntryService.deleteShoppingListEntryById(householdId, listEntryId);
        });

        Mockito.verify(shoppingListEntryRepository, Mockito.never()).deleteById(listEntryId);
    }
}
