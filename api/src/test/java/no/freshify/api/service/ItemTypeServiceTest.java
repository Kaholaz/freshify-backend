package no.freshify.api.service;

import no.freshify.api.exception.ItemTypeNotFoundException;
import no.freshify.api.exception.ItemTypesNotFoundException;
import no.freshify.api.model.ItemType;
import no.freshify.api.repository.ItemTypeRepository;
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

public class ItemTypeServiceTest {
    @Mock
    private ItemTypeRepository itemTypeRepository;

    @InjectMocks
    private ItemTypeService itemTypeService;

    private ItemType itemType;
    private final long id = 1L;
    private final String name = "TestItemType";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        itemType = new ItemType();
        itemType.setId(id);
        itemType.setName(name);
    }

    @Test
    public void testGetItemTypeById() throws ItemTypeNotFoundException {
        Mockito.when(itemTypeRepository.findById(id)).thenReturn(Optional.of(itemType));

        ItemType result = itemTypeService.getItemTypeById(id);

        assertEquals(itemType, result);
    }

    @Test
    public void testGetItemTypeByIdThrowsException() {
        Mockito.when(itemTypeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ItemTypeNotFoundException.class, () -> {
            itemTypeService.getItemTypeById(id);
        });
    }

    @Test
    public void testGetAllItemTypes() {
        Mockito.when(itemTypeRepository.findAll()).thenReturn(Collections.singletonList(itemType));

        List<ItemType> result = itemTypeService.getAllItemTypes();

        assertEquals(Collections.singletonList(itemType), result);
    }

    @Test
    public void testSearchItemTypes() throws ItemTypesNotFoundException {
        Mockito.when(itemTypeRepository.searchItemTypeByName(name)).thenReturn(Collections.singletonList(itemType));

        List<ItemType> result = itemTypeService.searchItemTypes(name);

        assertEquals(Collections.singletonList(itemType), result);
    }

    @Test
    public void testSearchItemTypesThrowsException() {
        Mockito.when(itemTypeRepository.searchItemTypeByName(name)).thenReturn(Collections.emptyList());

        assertThrows(ItemTypesNotFoundException.class, () -> {
            itemTypeService.searchItemTypes(name);
        });
    }
}
