package no.freshify.api.model.mapper;

import no.freshify.api.exception.IllegalItemParameterException;
import no.freshify.api.model.Item;
import no.freshify.api.model.ItemType;
import no.freshify.api.model.dto.InventoryItem;
import no.freshify.api.model.dto.ItemTypeDTO;
import no.freshify.api.model.dto.UpdateInventoryItem;
import no.freshify.api.model.dto.WastedItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.*;

@Mapper(uses = {UserMapper.class})
public abstract class ItemMapper {
    public abstract InventoryItem toItemDto(Item item);

    public abstract List<InventoryItem> toItemDtos(List<Item> items);

    public abstract ItemTypeDTO toItemTypeDTO(ItemType itemType);

    public abstract List<ItemTypeDTO> toItemTypeDTO(List<ItemType> itemTypes);

    @Mappings({
                    @Mapping(target = "id", source = "itemId"),
                    @Mapping(target = "status", source = "state")
            })
    public abstract Item toItem(UpdateInventoryItem updateInventoryItem) throws IllegalItemParameterException;

    public List<WastedItemDTO> toWastedItemDTO(List<Item> items) {
        Map<ItemType, Integer> occurrences = countOccurrences(items);
        Map<ItemType, Double> averageRemaining = toAverageRemaining(items, occurrences);
        List<WastedItemDTO> result = new ArrayList<>();

        for (Map.Entry<ItemType, Integer> entry : occurrences.entrySet()) {
            result.add(new WastedItemDTO(entry.getKey().getName(), entry.getValue(), averageRemaining.get(entry.getKey())));
        }
        return result;
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
}
