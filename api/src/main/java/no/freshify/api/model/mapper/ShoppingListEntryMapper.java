package no.freshify.api.model.mapper;

import no.freshify.api.model.ShoppingListEntry;
import no.freshify.api.model.dto.ShoppingListEntryRequest;
import no.freshify.api.model.dto.ShoppingListEntryResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = { UserMapper.class })
public abstract class ShoppingListEntryMapper {

    public abstract ShoppingListEntryResponse toShoppingListEntryResponse(ShoppingListEntry shoppingListEntry);
    public abstract List<ShoppingListEntryResponse> toShoppingListEntryResponse(List<ShoppingListEntry> shoppingListEntries);

    public abstract ShoppingListEntry fromShoppingListEntryRequest(ShoppingListEntryRequest shoppingListEntryRequest);
}
