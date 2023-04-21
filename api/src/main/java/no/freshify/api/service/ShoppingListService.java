package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.ShoppingListEntryAlreadyExistsException;
import no.freshify.api.model.Item;
import no.freshify.api.model.ShoppingListEntry;
import no.freshify.api.repository.ShoppingListEntryRepository;
import no.freshify.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingListService {

    private final ShoppingListEntryRepository shoppingListEntryRepository;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public void addItem(ShoppingListEntry shoppingListEntry) throws ShoppingListEntryAlreadyExistsException {
        logger.info("Adding item to shopping list");
        if (shoppingListEntryRepository.existsById(shoppingListEntry.getId())) {
            logger.warn("Shopping list entry already exists in the shopping list");
            throw new ShoppingListEntryAlreadyExistsException();
        }
        shoppingListEntryRepository.save(shoppingListEntry);
    }
}
