package no.freshify.api.model.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import no.freshify.api.exception.IllegalItemParameterException;
import no.freshify.api.model.Item;
import no.freshify.api.model.ItemType;
import no.freshify.api.model.dto.InventoryItem;
import no.freshify.api.model.dto.ItemTypeDTO;
import no.freshify.api.model.dto.UpdateInventoryItem;
import org.mapstruct.factory.Mappers;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-04-28T10:55:48+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
)
public class ItemMapperImpl extends ItemMapper {

    private final UserMapper userMapper = Mappers.getMapper( UserMapper.class );

    @Override
    public InventoryItem toItemDto(Item item) {
        if ( item == null ) {
            return null;
        }

        InventoryItem inventoryItem = new InventoryItem();

        inventoryItem.setId( item.getId() );
        inventoryItem.setLastChanged( item.getLastChanged() );
        inventoryItem.setBought( item.getBought() );
        inventoryItem.setRemaining( item.getRemaining() );
        inventoryItem.setSuggested( item.getSuggested() );
        inventoryItem.setStatus( item.getStatus() );
        inventoryItem.setType( item.getType() );
        inventoryItem.setAddedBy( userMapper.toUserFull( item.getAddedBy() ) );

        return inventoryItem;
    }

    @Override
    public List<InventoryItem> toItemDtos(List<Item> items) {
        if ( items == null ) {
            return null;
        }

        List<InventoryItem> list = new ArrayList<InventoryItem>( items.size() );
        for ( Item item : items ) {
            list.add( toItemDto( item ) );
        }

        return list;
    }

    @Override
    public ItemTypeDTO toItemTypeDTO(ItemType itemType) {
        if ( itemType == null ) {
            return null;
        }

        ItemTypeDTO itemTypeDTO = new ItemTypeDTO();

        itemTypeDTO.setId( itemType.getId() );
        itemTypeDTO.setName( itemType.getName() );

        return itemTypeDTO;
    }

    @Override
    public List<ItemTypeDTO> toItemTypeDTO(List<ItemType> itemTypes) {
        if ( itemTypes == null ) {
            return null;
        }

        List<ItemTypeDTO> list = new ArrayList<ItemTypeDTO>( itemTypes.size() );
        for ( ItemType itemType : itemTypes ) {
            list.add( toItemTypeDTO( itemType ) );
        }

        return list;
    }

    @Override
    public Item toItem(UpdateInventoryItem updateInventoryItem) throws IllegalItemParameterException {
        if ( updateInventoryItem == null ) {
            return null;
        }

        Item item = new Item();

        item.setId( updateInventoryItem.getItemId() );
        item.setStatus( updateInventoryItem.getState() );
        item.setRemaining( updateInventoryItem.getRemaining() );

        return item;
    }
}
