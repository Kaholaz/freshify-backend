package no.freshify.api.model.mapper;

import no.freshify.api.model.Item;
import no.freshify.api.model.ItemType;
import no.freshify.api.model.dto.InventoryItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {UserMapper.class})
public abstract class ItemMapper {
    public abstract InventoryItem toItemDto(Item item);

    public abstract ItemType toItemTypeDTO(ItemType itemType);

    public abstract List<ItemType> toItemTypeDTO(List<ItemType> itemTypes);
}
