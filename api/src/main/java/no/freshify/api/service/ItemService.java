package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.model.Item;
import no.freshify.api.repository.ItemRepository;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public void addItem(Item item) {
        itemRepository.save(item);
    }
}
