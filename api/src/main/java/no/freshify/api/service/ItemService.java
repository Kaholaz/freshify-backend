package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.ItemDoesNotBelongToHouseholdException;
import no.freshify.api.exception.ItemNotFoundException;
import no.freshify.api.model.Household;
import no.freshify.api.model.Item;
import no.freshify.api.model.ItemStatus;
import no.freshify.api.repository.ItemRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

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

    public void deleteItemById(long id) {
        itemRepository.deleteById(id);
    }

    public Item updateItem(Item item) {
        return itemRepository.save(item);
    }

    public Item addItem(Item item) {
        return itemRepository.save(item);
    }
}
