package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.ItemTypeNotFoundException;
import no.freshify.api.model.ItemType;
import no.freshify.api.repository.ItemTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemTypeService {
    private final ItemTypeRepository itemTypeRepository;

    Logger logger = LoggerFactory.getLogger(ItemTypeService.class);

    public ItemType getItemTypeById(long id) throws ItemTypeNotFoundException {
        ItemType itemType = itemTypeRepository.findById(id).orElse(null);
        if (itemType == null) {
            logger.warn("Item type not found");
            throw new ItemTypeNotFoundException();
        }
        return itemType;
    }

    public List<ItemType> getAllItemTypes() {
        return itemTypeRepository.findAll();
    }
}
