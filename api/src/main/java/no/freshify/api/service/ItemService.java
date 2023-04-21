package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
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

    public void addItem(Item item) {
        itemRepository.save(item);
    }
}
