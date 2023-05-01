package no.freshify.api.model.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import no.freshify.api.model.ItemType;
import no.freshify.api.model.ShoppingListEntry;
import no.freshify.api.model.dto.ShoppingListEntryEditRequest;
import no.freshify.api.model.dto.ShoppingListEntryRequest;
import no.freshify.api.model.dto.ShoppingListEntryResponse;
import org.mapstruct.factory.Mappers;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-01T11:30:05+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
)
public class ShoppingListEntryMapperImpl extends ShoppingListEntryMapper {

    private final UserMapper userMapper = Mappers.getMapper( UserMapper.class );

    @Override
    public ShoppingListEntryResponse toShoppingListEntryResponse(ShoppingListEntry shoppingListEntry) {
        if ( shoppingListEntry == null ) {
            return null;
        }

        ShoppingListEntryResponse shoppingListEntryResponse = new ShoppingListEntryResponse();

        shoppingListEntryResponse.setId( shoppingListEntry.getId() );
        shoppingListEntryResponse.setCount( shoppingListEntry.getCount() );
        shoppingListEntryResponse.setSuggested( shoppingListEntry.getSuggested() );
        shoppingListEntryResponse.setChecked( shoppingListEntry.getChecked() );
        shoppingListEntryResponse.setType( shoppingListEntry.getType() );
        shoppingListEntryResponse.setAddedBy( userMapper.toUserFull( shoppingListEntry.getAddedBy() ) );

        return shoppingListEntryResponse;
    }

    @Override
    public List<ShoppingListEntryResponse> toShoppingListEntryResponse(List<ShoppingListEntry> shoppingListEntries) {
        if ( shoppingListEntries == null ) {
            return null;
        }

        List<ShoppingListEntryResponse> list = new ArrayList<ShoppingListEntryResponse>( shoppingListEntries.size() );
        for ( ShoppingListEntry shoppingListEntry : shoppingListEntries ) {
            list.add( toShoppingListEntryResponse( shoppingListEntry ) );
        }

        return list;
    }

    @Override
    public ShoppingListEntry fromShoppingListEntryRequest(ShoppingListEntryRequest shoppingListEntryRequest) {
        if ( shoppingListEntryRequest == null ) {
            return null;
        }

        ShoppingListEntry shoppingListEntry = new ShoppingListEntry();

        shoppingListEntry.setType( shoppingListEntryRequestToItemType( shoppingListEntryRequest ) );
        shoppingListEntry.setCount( shoppingListEntryRequest.getCount() );
        shoppingListEntry.setSuggested( shoppingListEntryRequest.getSuggested() );

        return shoppingListEntry;
    }

    @Override
    public ShoppingListEntry fromShoppingListEntryEditRequest(ShoppingListEntryEditRequest shoppingListEntryEditRequest) {
        if ( shoppingListEntryEditRequest == null ) {
            return null;
        }

        ShoppingListEntry shoppingListEntry = new ShoppingListEntry();

        shoppingListEntry.setId( shoppingListEntryEditRequest.getId() );
        shoppingListEntry.setCount( shoppingListEntryEditRequest.getCount() );
        shoppingListEntry.setSuggested( shoppingListEntryEditRequest.getSuggested() );
        shoppingListEntry.setChecked( shoppingListEntryEditRequest.getChecked() );

        return shoppingListEntry;
    }

    protected ItemType shoppingListEntryRequestToItemType(ShoppingListEntryRequest shoppingListEntryRequest) {
        if ( shoppingListEntryRequest == null ) {
            return null;
        }

        ItemType itemType = new ItemType();

        itemType.setId( shoppingListEntryRequest.getItemTypeId() );

        return itemType;
    }
}
