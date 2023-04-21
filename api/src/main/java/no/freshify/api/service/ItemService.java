package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.ItemNotFoundException;
import no.freshify.api.model.Household;
import no.freshify.api.model.Item;
import no.freshify.api.repository.ItemRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public List<Item> getHouseholdItems(Household household) {
        return itemRepository.findItemsByHousehold(household);
    }

    public Item getItemById(long id) throws ItemNotFoundException {
        Item item = itemRepository.findById(id).orElse(null);
        if (item == null) {
            throw new ItemNotFoundException();
        }

        return item;
    }

    public void deleteItemById(long id) throws ItemNotFoundException {
        Item item = getItemById(id);
        itemRepository.delete(item);
    }

    public void addItem(Item item) {
        itemRepository.save(item);
    }
}
