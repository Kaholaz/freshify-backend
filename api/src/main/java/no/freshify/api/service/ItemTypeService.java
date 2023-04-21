package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.ItemTypeNotFoundException;
import no.freshify.api.model.ItemType;
import no.freshify.api.repository.ItemTypeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemTypeService {
    private final ItemTypeRepository itemTypeRepository;

    public ItemType getItemTypeById(long id) throws ItemTypeNotFoundException {
        ItemType itemType = itemTypeRepository.findById(id).orElse(null);
        if (itemType == null) {
            throw new ItemTypeNotFoundException();
        }
        return itemType;
    }
}
