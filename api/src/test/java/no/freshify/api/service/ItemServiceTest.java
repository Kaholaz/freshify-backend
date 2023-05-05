package no.freshify.api.service;

import no.freshify.api.exception.IllegalItemParameterException;
import no.freshify.api.exception.ItemDoesNotBelongToHouseholdException;
import no.freshify.api.exception.ItemNotFoundException;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.WastedItemDTO;
import no.freshify.api.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private final Long itemId = 1L;
    private final Long householdId = 1L;

    private Item item;
    private List<Item> wastedItems;
    private ItemType itemType1;
    private ItemType itemType2;
    private Date startDate;
    private Date lastChanged;
    private Household household;

    @BeforeEach
    public void setup() throws IllegalItemParameterException {
        MockitoAnnotations.openMocks(this);

        startDate = new Date(System.currentTimeMillis());
        lastChanged = new Date(System.currentTimeMillis() + 10L);

        household = new Household();
        household.setId(householdId);

        itemType1 = new ItemType();
        itemType2 = new ItemType();

        item = new Item();
        item.setId(itemId);
        item.setType(itemType1);
        item.setHousehold(household);
        item.setRemaining(0.4D);
        item.setStatus(ItemStatus.USED);
        item.setLastChanged(lastChanged);

        Item item2 = new Item();
        item2.setId(itemId);
        item2.setType(itemType1);
        item2.setHousehold(household);
        item2.setRemaining(0.6D);
        item2.setStatus(ItemStatus.USED);
        item2.setLastChanged(lastChanged);

        Item item3 = new Item();
        item3.setId(itemId);
        item3.setType(itemType2);
        item3.setHousehold(household);
        item3.setRemaining(0.1D);
        item3.setStatus(ItemStatus.USED);
        item3.setLastChanged(lastChanged);

        wastedItems = List.of(item, item2, item3);
    }

    @Test
    public void testGetHouseholdItems() {
        Mockito.when(itemRepository.findItemsByHousehold(household)).thenReturn(Collections.singletonList(item));

        List<Item> result = itemService.getHouseholdItems(household);

        assertEquals(Collections.singletonList(item), result);
    }

    @Test
    public void testGetItemById() throws ItemNotFoundException {
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Item result = itemService.getItemById(itemId);

        assertEquals(item, result);
    }

    @Test
    public void testGetItemByIdThrowsException() {
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> {
            itemService.getItemById(itemId);
        });
    }

    @Test
    public void testGetItemByIdAndHousehold() throws ItemNotFoundException, ItemDoesNotBelongToHouseholdException {
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.findByIdAndHousehold(itemId, household)).thenReturn(item);

        Item result = itemService.getItemByIdAndHousehold(itemId, household);

        assertEquals(item, result);
    }

    @Test
    public void testGetItemByIdAndHouseholdThrowsNotFoundException() {
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> {
            itemService.getItemByIdAndHousehold(itemId, household);
        });
    }

    @Test
    public void testGetItemByIdAndHouseholdThrowsDoesNotBelongToHouseholdException() {
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.findByIdAndHousehold(itemId, household)).thenReturn(null);

        assertThrows(ItemDoesNotBelongToHouseholdException.class, () -> {
            itemService.getItemByIdAndHousehold(itemId, household);
        });
    }

    @Test
    public void testDeleteItemById() {
        itemService.deleteItemById(itemId);

        Mockito.verify(itemRepository, Mockito.times(1)).deleteById(itemId);
    }

    @Test
    public void testUpdateItem() {
        itemService.updateItem(item);

        Mockito.verify(itemRepository, Mockito.times(1)).save(item);
    }

    @Test
    public void testAddItem() throws ItemNotFoundException {
        itemService.addItem(item);

        Mockito.verify(itemRepository, Mockito.times(1)).save(item);
    }

    @Test
    public void testFindWastedItemsInTimeInterval() {
        Mockito.when(itemRepository.findItemsByHouseholdAndStatusAndLastChangedBetweenAndRemainingGreaterThan
                (any(Household.class), any(ItemStatus.class), any(Date.class), any(Date.class), any(Double.class)))
                .thenReturn(wastedItems);

        Date endDate = new Date(System.currentTimeMillis() + 1000000L);
        List<Item> result = itemService.findAllUsedItemsInTimeInterval(household, startDate, endDate);

        assertEquals(0, result.size());
        result.forEach(item -> {
            assertEquals(household, item.getHousehold());
            assertTrue(item.getRemaining() > 0D);
            assertTrue(item.getLastChanged().getTime() > startDate.getTime()
                    && item.getLastChanged().getTime() < endDate.getTime());
            assertEquals(ItemStatus.USED, item.getStatus());
        });
    }
}
