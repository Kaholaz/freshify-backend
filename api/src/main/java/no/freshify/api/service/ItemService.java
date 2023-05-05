package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.ItemDoesNotBelongToHouseholdException;
import no.freshify.api.exception.ItemNotFoundException;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.WastedItemDTO;
import no.freshify.api.model.mapper.ItemMapper;
import no.freshify.api.model.mapper.ItemMapperImpl;
import no.freshify.api.repository.ItemRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper = new ItemMapperImpl();

    Logger logger = LoggerFactory.getLogger(ItemService.class);

    public List<Item> getHouseholdItems(Household household) {
        return itemRepository.findItemsByHousehold(household);
    }

    public List<Item> getInventoryItems(Household household) {
        return itemRepository.findItemsByHouseholdAndStatus(household, ItemStatus.INVENTORY);
    }

    public Item getItemById(long id) throws ItemNotFoundException {
        Item item = itemRepository.findById(id).orElse(null);
        if (item == null) {
            logger.warn("Item not found");
            throw new ItemNotFoundException();
        }
        return item;
    }

    public Item getItemByIdAndHousehold(long id, Household household) throws ItemDoesNotBelongToHouseholdException, ItemNotFoundException {
        this.getItemById(id);

        Item item = itemRepository.findByIdAndHousehold(id, household);
        if (item == null) {
            logger.warn("Item does not belong to household");
            throw new ItemDoesNotBelongToHouseholdException();
        }

        return item;
    }

    public List<Item> findAllUsedItemsInTimeInterval(Household household, Date startDate, Date endDate) {
        return itemRepository.findItemsByHouseholdAndStatusAndLastChangedBetween
                (household, ItemStatus.USED, startDate, endDate);
    }

    public List<Item> findAllWastedItems(Household household) {
        return itemRepository.findItemsByHouseholdAndStatusAndRemainingGreaterThan(household, ItemStatus.USED, 0D);
    }


    public List<Item> findAllUsedItems(Household household) {
        return itemRepository.findItemsByHouseholdAndStatus(household, ItemStatus.USED);
    }

    public void deleteItemById(long id) {
        itemRepository.deleteById(id);
    }

    public Item updateItem(Item item) {
        return itemRepository.save(item);
    }

    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    public Double getAverageWaste(List<Item> wastedItems) {
        return wastedItems.stream().mapToDouble(Item::getRemaining).average().orElse(0D);
    }

    public List<Item> findByTypeAndHouseholdAndStatus(ItemType type, Household household, ItemStatus status) {
        return itemRepository.findByTypeAndHouseholdAndStatus(type, household, status);
    }

    public HashSet<ItemType> getUniqueItemTypes(List<Item> items) {
        return items.stream()
                .map(Item::getType)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
