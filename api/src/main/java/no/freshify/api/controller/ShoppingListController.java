package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.HouseholdNotFoundException;
import no.freshify.api.exception.ItemTypeNotFoundException;
import no.freshify.api.exception.ShoppingListEntryAlreadyExistsException;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.ShoppingListEntryRequest;
import no.freshify.api.service.HouseholdService;
import no.freshify.api.service.ItemTypeService;
import no.freshify.api.service.ShoppingListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/household/{id}/shoppinglist")
@RequiredArgsConstructor
public class ShoppingListController {

    private final HouseholdService householdService;
    private final ShoppingListService shoppingListService;
    private final ItemTypeService itemTypeService;

    private final Logger logger = LoggerFactory.getLogger(InventoryController.class);


    /**
     * Adds an item to the given household's shopping list
     * @param householdId The household whose shopping list we want to add an item to
     * @param requestBody The item to be added
     * @return
     * @throws HouseholdNotFoundException If the household was not found
     * @throws ItemTypeNotFoundException If the item type was not found
     * @throws ShoppingListEntryAlreadyExistsException If the shopping list entry already exists in the shopping list
     */
    //TODO: legg til 'addedBy' i shoppingListEntry
    @PostMapping()
    public ResponseEntity<ShoppingListEntry> addItem(@PathVariable("id") long householdId,
                                                       @RequestBody ShoppingListEntryRequest requestBody)
            throws HouseholdNotFoundException, ItemTypeNotFoundException, ShoppingListEntryAlreadyExistsException {

        // Find the household by ID
        Household household = householdService.findHouseholdByHouseholdId(householdId);

        // Create a new ShoppingListEntry object
        ShoppingListEntry shoppingListEntry = new ShoppingListEntry();
        shoppingListEntry.setType(itemTypeService.getItemTypeById(requestBody.getItemTypeId()));
        shoppingListEntry.setCount(requestBody.getCount());
        shoppingListEntry.setSuggested(requestBody.getSuggested());
        shoppingListEntry.setHousehold(household);

        // Add the new ShoppingListEntry to the household's shopping list
        shoppingListService.addItem(shoppingListEntry);

        // Return the updated shopping list
        return ResponseEntity.ok(shoppingListEntry);
    }
}
