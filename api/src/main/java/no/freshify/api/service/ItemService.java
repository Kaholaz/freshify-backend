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

    public List<Item> findWastedItemsInTimeInterval(Household household, Date startDate, Date endDate) {
        return itemRepository.findItemsByHouseholdAndStatusAndLastChangedBetweenAndRemainingGreaterThan
                (household, ItemStatus.USED, startDate, endDate, 0D);
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

    public Map<ItemType, Integer> countOccurrences(List<Item> list) {
        Map<ItemType, Integer> occurrences = new HashMap<>();

        for (Item item : list) {
            if (occurrences.containsKey(item.getType())) {
                occurrences.put(item.getType(), occurrences.get(item.getType()) + 1);
            } else {
                occurrences.put(item.getType(), 1);
            }
        }
        return occurrences;
    }

    public Map<ItemType, Double> toAverageRemaining(List<Item> items, Map<ItemType, Integer> occurrences) {
        // calculate total remaining for each item type:
        Map<ItemType, Double> totalRemaining = new HashMap<>();
        for (Item item : items) {
            if (totalRemaining.containsKey(item.getType())) {
                totalRemaining.put(item.getType(), totalRemaining.get(item.getType()) + item.getRemaining());
            } else {
                totalRemaining.put(item.getType(), item.getRemaining());
            }
        }

        // calculate average remaining for each item type:
        Map<ItemType, Double> averageRemaining = new HashMap<>();
        for (Map.Entry<ItemType, Double> entry : totalRemaining.entrySet()) {
            averageRemaining.put(entry.getKey(), entry.getValue() / occurrences.get(entry.getKey()).doubleValue());
        }

        return averageRemaining;
    }

    public <T extends Number> List<Map.Entry<ItemType, Number>> sortMapByNumberValueDescending(Map<ItemType, T> map,
                                                                                               Class<T> numberType) {
        List<Map.Entry<ItemType, T>> list = new ArrayList<>(map.entrySet());

        if (Integer.class.equals(numberType)) {
            list.sort((o1, o2) -> Integer.compare(o2.getValue().intValue(), o1.getValue().intValue()));
        } else if (Double.class.equals(numberType)) {
            list.sort((o1, o2) -> Double.compare(o2.getValue().doubleValue(), o1.getValue().doubleValue()));
        }

        // Convert to list of Map.Entry objects with Number as value type:
        List<Map.Entry<ItemType, Number>> result = new ArrayList<>();
        for (Map.Entry<ItemType, T> entry : list) {
            result.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    public List<WastedItemDTO> getSortedItemsByWaste(List<Item> items, ItemSortMethod sortBy) {
        Map<ItemType, Integer> occurrences = countOccurrences(items);

        List<Map.Entry<ItemType, Number>> result = switch (sortBy) {
            case COUNT -> sortMapByNumberValueDescending(occurrences, Integer.class);
            case PERCENTAGE -> sortMapByNumberValueDescending(toAverageRemaining(items, occurrences), Double.class);
        };
        return result
                .stream()
                .map(e -> new WastedItemDTO(itemMapper.toItemTypeDTO(e.getKey()), e.getValue()))
                .toList();
    }

    public Item findByTypeAndHousehold(ItemType type, Household household) {
        return itemRepository.findByTypeAndHousehold(type, household);
    }
}
