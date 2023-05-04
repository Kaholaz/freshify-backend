package no.freshify.api.model.mapper;

import no.freshify.api.model.ShoppingListEntry;
import no.freshify.api.model.dto.ShoppingListEntryEditRequest;
import no.freshify.api.model.dto.ShoppingListEntryRequest;
import no.freshify.api.model.dto.ShoppingListEntryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses = { UserMapper.class })
public abstract class ShoppingListEntryMapper {

    public abstract ShoppingListEntryResponse toShoppingListEntryResponse(ShoppingListEntry shoppingListEntry);
    public abstract List<ShoppingListEntryResponse> toShoppingListEntryResponse(List<ShoppingListEntry> shoppingListEntries);

    @Mappings({
            @Mapping(target = "type.id", source = "itemTypeId"),
    })
    public abstract ShoppingListEntry fromShoppingListEntryRequest(ShoppingListEntryRequest shoppingListEntryRequest);

    public abstract ShoppingListEntry fromShoppingListEntryEditRequest(ShoppingListEntryEditRequest shoppingListEntryEditRequest);
}
