package no.freshify.api.controller;

import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.*;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.ShoppingListEntryEditRequest;
import no.freshify.api.model.dto.ShoppingListEntryRequest;
import no.freshify.api.model.dto.ShoppingListEntryResponse;
import no.freshify.api.model.mapper.HouseholdMapper;
import no.freshify.api.model.mapper.ShoppingListEntryMapper;
import no.freshify.api.repository.HouseholdRepository;
import no.freshify.api.repository.ShoppingListEntryRepository;
import no.freshify.api.repository.UserRepository;
import no.freshify.api.security.AuthenticationService;
import no.freshify.api.service.HouseholdService;
import no.freshify.api.service.ItemTypeService;
import no.freshify.api.service.ShoppingListEntryService;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/household/{id}/shoppinglist")
@RequiredArgsConstructor
public class ShoppingListController {

    private final HouseholdService householdService;
    private final ShoppingListEntryService shoppingListEntryService;
    private final ItemTypeService itemTypeService;
    private final AuthenticationService authenticationService;

    private final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    private final ShoppingListEntryRepository shoppingListEntryRepository;
    private final ShoppingListEntryMapper shoppingListEntryMapper = Mappers.getMapper(ShoppingListEntryMapper.class);

    private final HouseholdRepository householdRepository;
    private final UserRepository userRepository;

    /**
     * Adds an item to the given household's shopping list
     * @param householdId The household whose shopping list we want to add an item to
     * @param requestBody The item to be added
     * @return
     * @throws HouseholdNotFoundException If the household was not found
     * @throws ItemTypeNotFoundException If the item type was not found
     * @throws ShoppingListEntryAlreadyExistsException If the shopping list entry already exists in the shopping list
     */
    @PostMapping
    public ResponseEntity<ShoppingListEntryResponse> addItem(@PathVariable("id") long householdId,
                                                     @RequestBody ShoppingListEntryRequest requestBody)
            throws ItemTypeNotFoundException, ShoppingListEntryAlreadyExistsException, HouseholdNotFoundException, ShoppingListEntryNotFoundException {
        User loggedInUser = authenticationService.getLoggedInUser();
        Household household = householdService.findHouseholdByHouseholdId(householdId);

        // Create a new ShoppingListEntry object
        ShoppingListEntry shoppingListEntry = new ShoppingListEntry();
        shoppingListEntry.setType(itemTypeService.getItemTypeById(requestBody.getItemTypeId()));
        shoppingListEntry.setCount(requestBody.getCount());
        shoppingListEntry.setSuggested(requestBody.getSuggested());

        shoppingListEntry.setHousehold(household);
        shoppingListEntry.setAddedBy(loggedInUser);

        // Add the new ShoppingListEntry to the household's shopping list
        shoppingListEntryService.addItem(shoppingListEntry);

        Long id = shoppingListEntryService.findShoppingListEntryByItemType(householdId, shoppingListEntry.getType().getId()).getId();
        shoppingListEntry.setId(id);

        // Return the updated shopping list
        return ResponseEntity.status(HttpStatus.CREATED).body(shoppingListEntryMapper.toShoppingListEntryResponse(shoppingListEntry));
    }

    /**
     * Deletes a shopping list entry from the shopping list of a given household.
     * @param householdId The household to delete an entry from
     * @param listEntryId The entry to delete from the shopping list
     * @return
     * @throws ShoppingListEntryNotFoundException If the shopping list entry was not found
     * in the given household's shopping list
     * @throws HouseholdNotFoundException If the given household was not found
     */
    @PreAuthorize("isAuthenticated()")
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
     * @return
     * @throws InvalidItemCountException If the new item count is invalid
     * @throws HouseholdNotFoundException If the household is not found
     * @throws ItemTypeNotFoundException If the new item type is invalid
     */
    @PutMapping
    public ResponseEntity<HttpStatus> updateShoppingListEntry(@PathVariable("id") long householdId,
                                                                @RequestBody ShoppingListEntryEditRequest requestBody)
            throws InvalidItemCountException, HouseholdNotFoundException, ItemTypeNotFoundException, ShoppingListEntryNotFoundException {
        Household household = householdService.findHouseholdByHouseholdId(householdId);

        ShoppingListEntry updatedEntry = new ShoppingListEntry();
        updatedEntry.setId(shoppingListEntryService.findShoppingListEntryByItemType(householdId, requestBody.getId()).getId());
        updatedEntry.setType(itemTypeService.getItemTypeById(requestBody.getId()));
        updatedEntry.setCount(requestBody.getCount());
        updatedEntry.setSuggested(requestBody.getSuggested());
        updatedEntry.setChecked(requestBody.getChecked());
        updatedEntry.setHousehold(household);

        shoppingListEntryService.updateShoppingListEntry(updatedEntry);

        return ResponseEntity.ok().build();
    }

    /**
     * Gets the shopping list items of a given household
     * @param householdId The household to get shopping list items from
     * @return A list of shopping list items belonging to the household
     * @throws HouseholdNotFoundException If the household is not found
     */
    @GetMapping
    public ResponseEntity<List<ShoppingListEntryResponse>> getShoppingList(@PathVariable("id") long householdId)
            throws HouseholdNotFoundException {

        List<ShoppingListEntry> entries = shoppingListEntryService.getShoppingList(householdId);
        List<ShoppingListEntryResponse> responseObjects = shoppingListEntryMapper.toShoppingListEntryResponse(entries);

        return ResponseEntity.ok(responseObjects);
    }

}
