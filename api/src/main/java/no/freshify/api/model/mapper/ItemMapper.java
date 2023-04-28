package no.freshify.api.model.mapper;

import no.freshify.api.exception.IllegalItemParameterException;
import no.freshify.api.model.Item;
import no.freshify.api.model.ItemType;
import no.freshify.api.model.dto.InventoryItem;
import no.freshify.api.model.dto.ItemTypeDTO;
import no.freshify.api.model.dto.UpdateInventoryItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

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
}
