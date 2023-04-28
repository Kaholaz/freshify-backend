package no.freshify.api.service;

import no.freshify.api.exception.ItemDoesNotBelongToHouseholdException;
import no.freshify.api.exception.ItemNotFoundException;
import no.freshify.api.model.Household;
import no.freshify.api.model.Item;
import no.freshify.api.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private final Long itemId = 1L;
    private final Long householdId = 1L;

    private Item item;
    private Household household;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        household = new Household();
        household.setId(householdId);

        item = new Item();
        item.setId(itemId);
        item.setHousehold(household);
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
}
