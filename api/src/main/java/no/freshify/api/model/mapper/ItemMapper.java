package no.freshify.api.model.mapper;

import no.freshify.api.model.Item;
import no.freshify.api.model.dto.InventoryItem;
import org.mapstruct.Mapper;

@Mapper(uses = {UserMapper.class})
public abstract class ItemMapper {
    public abstract InventoryItem toItemDto(Item item);

}
