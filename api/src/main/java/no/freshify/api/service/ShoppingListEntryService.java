package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.HouseholdNotFoundException;
import no.freshify.api.exception.InvalidItemCountException;
import no.freshify.api.exception.ShoppingListEntryAlreadyExistsException;
import no.freshify.api.exception.ShoppingListEntryNotFoundException;
import no.freshify.api.model.Household;
import no.freshify.api.model.ItemType;
import no.freshify.api.model.ShoppingListEntry;
import no.freshify.api.repository.HouseholdRepository;
import no.freshify.api.repository.ShoppingListEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShoppingListEntryService {

    private final ShoppingListEntryRepository shoppingListEntryRepository;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final HouseholdRepository householdRepository;

    public void addItem(ShoppingListEntry shoppingListEntry) throws ShoppingListEntryAlreadyExistsException {
        logger.info("Adding item to shopping list");
        if (shoppingListEntryRepository.existsById(shoppingListEntry.getId())) {
            logger.warn("Shopping list entry already exists in the shopping list");
            throw new ShoppingListEntryAlreadyExistsException();
        }
        shoppingListEntryRepository.save(shoppingListEntry);
    }

    public void updateShoppingListEntry(ShoppingListEntry updatedEntry) throws InvalidItemCountException {
        logger.info("Updating shopping list entry");
        if (updatedEntry.getCount() <= 0) {
            logger.warn("Invalid operation. More items are removed than in shopping list.");
            throw new InvalidItemCountException();
        }
        shoppingListEntryRepository.save(updatedEntry);
    }

    public ShoppingListEntry findShoppingListEntryByItemType(long householdId, long itemTypeId)
            throws HouseholdNotFoundException, ShoppingListEntryNotFoundException {
        // Find list of shopping list items in given household
        List<ShoppingListEntry> shoppingListEntries = getShoppingList(householdId);

        if (shoppingListEntries == null) {
            logger.warn("A shopping list entry with given type was not found in the given household");
            throw new ShoppingListEntryNotFoundException();
        }

        // Find item with given itemTypeId
        Optional<ShoppingListEntry> found = shoppingListEntries.stream()
                .filter(item -> item.getType().getId() == itemTypeId)
                .findFirst();

        if (found.isPresent()) {
            return found.get();
        } else {
            logger.warn("A shopping list entry with the given item type was not found in the given household");
            throw new ShoppingListEntryNotFoundException();
        }
    }

    public List<ShoppingListEntry> getShoppingList(long householdId) throws HouseholdNotFoundException {
        logger.info("Getting shopping list");
        if (!householdRepository.existsById(householdId)) {
            logger.warn("Household not found");
            throw new HouseholdNotFoundException();
        }

        return shoppingListEntryRepository.findByHouseholdId(householdId);
    }
}
