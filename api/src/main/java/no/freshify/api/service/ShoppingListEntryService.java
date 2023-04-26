package no.freshify.api.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import no.freshify.api.exception.HouseholdNotFoundException;
import no.freshify.api.exception.InvalidItemCountException;
import no.freshify.api.exception.ShoppingListEntryAlreadyExistsException;
import no.freshify.api.exception.ShoppingListEntryNotFoundException;
import no.freshify.api.model.Household;
import no.freshify.api.model.Item;
import no.freshify.api.model.ItemType;
import no.freshify.api.model.ShoppingListEntry;
import no.freshify.api.repository.HouseholdRepository;
import no.freshify.api.repository.ShoppingListEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Getter
@RequiredArgsConstructor
public class ShoppingListEntryService {

    private final ShoppingListEntryRepository shoppingListEntryRepository;

    private final ItemService itemService;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final HouseholdService householdService;

    public ShoppingListEntry addItem(ShoppingListEntry shoppingListEntry) throws ShoppingListEntryAlreadyExistsException {
        logger.info("Adding item to shopping list");
        if (shoppingListEntry.getId() != null &&
                shoppingListEntryRepository.existsById(shoppingListEntry.getId())) {
            logger.warn("Shopping list entry already exists in the shopping list");
            throw new ShoppingListEntryAlreadyExistsException();
        }
        return shoppingListEntryRepository.save(shoppingListEntry);
    }

    public void updateShoppingListEntry(ShoppingListEntry updatedEntry) throws InvalidItemCountException, ShoppingListEntryNotFoundException {
        logger.info("Updating shopping list entry");
        if (updatedEntry.getCount() <= 0) {
            logger.warn("Invalid operation. More items are removed than in shopping list.");
            throw new InvalidItemCountException();
        }
        ShoppingListEntry oldEntry = shoppingListEntryRepository.findById(updatedEntry.getId()).orElse(null);
        if (oldEntry == null) {
            logger.warn("Shopping list entry not found");
            throw new ShoppingListEntryNotFoundException();
        }

        if (updatedEntry.getHousehold() == null) updatedEntry.setHousehold(oldEntry.getHousehold());
        if (updatedEntry.getType() == null) updatedEntry.setType(oldEntry.getType());
        if (updatedEntry.getAddedBy() == null) updatedEntry.setAddedBy(oldEntry.getAddedBy());

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
        if (!householdService.householdExists(householdId)) {
            logger.warn("Household not found");
            throw new HouseholdNotFoundException();
        }

        return shoppingListEntryRepository.findByHouseholdId(householdId);
    }

    public ShoppingListEntry findShoppingListEntryById(long listEntryId) throws ShoppingListEntryNotFoundException {
        logger.info("Getting shopping list entry");

        Optional<ShoppingListEntry> found = shoppingListEntryRepository.findById(listEntryId);

        if (found.isPresent()) {
            return found.get();
        } else {
            logger.warn("Shopping list entry not found");
            throw new ShoppingListEntryNotFoundException();
        }
    }

    public void deleteShoppingListEntryById(long householdId, long listEntryId)
            throws ShoppingListEntryNotFoundException, HouseholdNotFoundException {
        logger.info("Deleting a shopping list entry");

        List<ShoppingListEntry> shoppingListEntries = getShoppingList(householdId);

        Optional<ShoppingListEntry> entry = shoppingListEntries.stream()
                .filter(o -> o.getId() == listEntryId)
                .findFirst();

        if (entry.isPresent()) {
            shoppingListEntryRepository.deleteById(listEntryId);
        } else {
            logger.warn("Shopping list entry not found");
            throw new ShoppingListEntryNotFoundException();
        }
    }

    public void moveAllCheckedToInventory(long householdId) throws HouseholdNotFoundException {
        logger.info("Moving checked items to inventory for household with id " + householdId);
        List<ShoppingListEntry> shoppingListEntries = getShoppingList(householdId);

        shoppingListEntries.stream().filter(ShoppingListEntry::getChecked)
                .forEach(entry -> {
                    try {
                        moveEntryToInventory(entry.getId());
                    } catch (ShoppingListEntryNotFoundException e) {
                        logger.error("Shopping list entry not found while moving checked items to inventory");
                    }
                });
    }

    public void moveEntryToInventory(long shoppingListEntryId) throws ShoppingListEntryNotFoundException {
        logger.info("Moving shopping list entry with id " + shoppingListEntryId + " to inventory");
        ShoppingListEntry shoppingListEntry = shoppingListEntryRepository.findById(shoppingListEntryId).orElse(null);
        if (shoppingListEntry == null) {
            logger.warn("Shopping list entry not found");
            throw new ShoppingListEntryNotFoundException();
        }


        for (int i = 0; i < shoppingListEntry.getCount(); i++) {
            Item item = new Item();
            item.setType(shoppingListEntry.getType());
            item.setHousehold(shoppingListEntry.getHousehold());
            item.setAddedBy(shoppingListEntry.getAddedBy());
            itemService.addItem(item);
        }
    }
}
