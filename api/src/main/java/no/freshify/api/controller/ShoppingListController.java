package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;

import no.freshify.api.exception.*;

import no.freshify.api.model.*;
import no.freshify.api.model.dto.ShoppingListEntryEditRequest;
import no.freshify.api.model.dto.ShoppingListEntryRequest;
import no.freshify.api.model.dto.ShoppingListEntryResponse;
import no.freshify.api.model.mapper.ShoppingListEntryMapper;

import no.freshify.api.repository.HouseholdRepository;
import no.freshify.api.repository.ShoppingListEntryRepository;
import no.freshify.api.repository.UserRepository;

import no.freshify.api.security.AuthenticationService;
import no.freshify.api.security.PermissionEvaluatorImpl;
import no.freshify.api.service.HouseholdService;
import no.freshify.api.service.ItemTypeService;
import no.freshify.api.service.ShoppingListEntryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/household/{id}/shoppinglist")
@RequiredArgsConstructor
public class ShoppingListController {

    private final HouseholdService householdService;
    private final ShoppingListEntryService shoppingListEntryService;
    private final AuthenticationService authenticationService;
    private final ItemTypeService itemTypeService;

    private final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    private final ShoppingListEntryMapper shoppingListEntryMapper = Mappers.getMapper(ShoppingListEntryMapper.class);

    /**
     * Adds an item to the given household's shopping list
     * @param householdId The household whose shopping list we want to add an item to
     * @param requestBody The item to be added
     * @return The item that was added
     * @throws HouseholdNotFoundException If the household was not found
     * @throws ItemTypeNotFoundException If the item type was not found
     * @throws ShoppingListEntryAlreadyExistsException If the shopping list entry already exists in the shopping list
     */
    @PreAuthorize("hasPermission(#householdId, 'household', '')")
    @PostMapping
    public ResponseEntity<Object> addItem(@PathVariable("id") long householdId,
                                                     @RequestBody ShoppingListEntryRequest requestBody)
            throws ShoppingListEntryAlreadyExistsException, HouseholdNotFoundException, ItemTypeNotFoundException {
        // Only allow non-suggested items to be added by superusers
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PermissionEvaluatorImpl permissionEvaluator = new PermissionEvaluatorImpl();
        boolean isSuperuser = permissionEvaluator.hasPermission(auth, householdId, "household", "SUPERUSER");
        if (!requestBody.getSuggested() && !isSuperuser)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only superusers can add items to the shopping list");

        User loggedInUser = authenticationService.getLoggedInUser();
        Household household = householdService.findHouseholdByHouseholdId(householdId);

        var shoppingListEntry = shoppingListEntryMapper.fromShoppingListEntryRequest(requestBody);
        shoppingListEntry.setHousehold(household);
        shoppingListEntry.setAddedBy(loggedInUser);

        var itemType = itemTypeService.getItemTypeById(shoppingListEntry.getType().getId());
        shoppingListEntry.setType(itemType);

        var newShoppingListEntry = shoppingListEntryService.addItem(shoppingListEntry);
        return ResponseEntity.status(HttpStatus.CREATED).body(shoppingListEntryMapper.toShoppingListEntryResponse(newShoppingListEntry));
    }

    /**
     * Deletes a shopping list entry from the shopping list of a given household.
     * @param householdId The household to delete an entry from
     * @param listEntryId The entry to delete from the shopping list
     * @return 204 No Content
     * @throws ShoppingListEntryNotFoundException If the shopping list entry was not found
     * in the given household's shopping list
     * @throws HouseholdNotFoundException If the given household was not found
     */
    @PreAuthorize("hasPermission(#householdId, 'household', 'SUPERUSER')")
    @DeleteMapping("/{listEntryId}")
    public ResponseEntity<HttpStatus> deleteShoppingListEntry(@PathVariable("id") long householdId,
                                                              @PathVariable("listEntryId") long listEntryId)
            throws ShoppingListEntryNotFoundException, HouseholdNotFoundException {
        shoppingListEntryService.deleteShoppingListEntryById(householdId, listEntryId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates an entry in a shopping list which belongs to given household.
     * @param householdId The household whose shopping list to edit an item in
     * @param requestBody The new shopping list item
     * @return 200 OK
     * @throws InvalidItemCountException If the new item count is invalid
     * @throws ShoppingListEntryNotFoundException If the shopping list entry was not found
     */
    @PreAuthorize("hasPermission(#householdId, 'household', 'SUPERUSER')")
    @PutMapping
    public ResponseEntity<Object> updateShoppingListEntry(@PathVariable("id") long householdId,
                                                                @RequestBody ShoppingListEntryEditRequest requestBody)
            throws InvalidItemCountException, ShoppingListEntryNotFoundException {
        var oldEntry = shoppingListEntryService.findShoppingListEntryById(requestBody.getId());

        if (oldEntry.getHousehold().getId() != householdId)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The item is not part of the given household");

        oldEntry.setChecked(requestBody.getChecked());
        oldEntry.setSuggested(requestBody.getSuggested());
        oldEntry.setCount(requestBody.getCount());

        ShoppingListEntry updatedEntry = shoppingListEntryMapper.fromShoppingListEntryEditRequest(requestBody);
        shoppingListEntryService.updateShoppingListEntry(updatedEntry);
        return ResponseEntity.ok().build();
    }

    /**
     * Gets the shopping list items of a given household
     * @param householdId The household to get shopping list items from
     * @return A list of shopping list items belonging to the household
     * @throws HouseholdNotFoundException If the household is not found
     */
    @PreAuthorize("hasPermission(#householdId, 'household', '')")
    @GetMapping
    public ResponseEntity<List<ShoppingListEntryResponse>> getShoppingList(@PathVariable("id") long householdId)
            throws HouseholdNotFoundException {

        List<ShoppingListEntry> entries = shoppingListEntryService.getShoppingList(householdId);
        List<ShoppingListEntryResponse> responseObjects = shoppingListEntryMapper.toShoppingListEntryResponse(entries);

        return ResponseEntity.ok(responseObjects);
    }

    /**
     * Moves all checked items from the shopping list to the inventory of a given household
     * @param householdId The household to move items from
     * @return 200 OK
     * @throws HouseholdNotFoundException If the household was not found
     */
    @PreAuthorize("hasPermission(#householdId, 'household', 'SUPERUSER')")
    @PostMapping("/buy")
    public ResponseEntity<HttpStatus> buyItems(@PathVariable("id") long householdId)
            throws HouseholdNotFoundException {
        shoppingListEntryService.moveAllCheckedToInventory(householdId);
        return ResponseEntity.ok().build();
    }
}
