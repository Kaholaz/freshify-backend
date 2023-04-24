package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.*;
import no.freshify.api.model.Household;
import no.freshify.api.model.Item;
import no.freshify.api.model.ItemStatus;
import no.freshify.api.model.User;
import no.freshify.api.model.dto.InventoryItem;
import no.freshify.api.model.mapper.ItemMapper;
import no.freshify.api.model.mapper.ItemMapperImpl;
import no.freshify.api.security.UserDetailsImpl;
import no.freshify.api.service.HouseholdService;
import no.freshify.api.service.ItemService;
import no.freshify.api.service.ItemTypeService;
import no.freshify.api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/household")
@RequiredArgsConstructor
public class InventoryController {
    private final HouseholdService householdService;
    private final UserService userService;
    private final ItemTypeService itemTypeService;
    private final ItemService itemService;
    private final ItemMapper itemMapper = new ItemMapperImpl();

    Logger logger = LoggerFactory.getLogger(InventoryController.class);

    /**
     * Adds inventory items to a household
     * @param householdId The id of the household to add the items to
     * @param requestBody A list of objects each containing the itemTypeId and count of the item to add
     * @return A list of inventory items that were added
     * @throws UserNotFoundException If the user is not found
     * @throws HouseholdNotFoundException If the household is not found
     * @throws ItemTypeNotFoundException If the item type is not found
     */
    @PreAuthorize("hasPermission(#householdId, 'Household', 'SUPERUSER')")
    @PostMapping("/{id}/inventory")
    public ResponseEntity<List<InventoryItem>> addInventoryItem(@PathVariable("id") long householdId, @RequestBody List<Map<String, Object>> requestBody, @AuthenticationPrincipal UserDetailsImpl userDetails) throws UserNotFoundException, HouseholdNotFoundException, ItemTypeNotFoundException {
        User user = userService.getUserById(userDetails.getId());

        logger.info("Adding inventory items to household with id: " + householdId);

        Household household = householdService.findHouseholdByHouseholdId(householdId);

        ArrayList<InventoryItem> inventoryItems = new ArrayList<>();
        for (Map<String, Object> item : requestBody) {
            long itemTypeId = Long.parseLong(item.get("itemTypeId").toString());
            long count = Long.parseLong(item.get("count").toString());

            for (int i = 0; i < count; i++) {
                Item newItem = new Item();
                newItem.setHousehold(household);
                newItem.setAddedBy(user);
                newItem.setType(itemTypeService.getItemTypeById(itemTypeId));
                newItem.setStatus(ItemStatus.INVENTORY);

                itemService.addItem(newItem);
                inventoryItems.add(itemMapper.toItemDto(newItem));
            }
        }

        logger.info("Added inventory items");

        return ResponseEntity.ok(inventoryItems);
    }

    /**
     * Gets the inventory items for a household
     * @param householdId The id of the household to get the inventory items from
     * @return A list of inventory items
     * @throws HouseholdNotFoundException If the household is not found
     */
    @PreAuthorize("hasPermission(#householdId, 'Household', '')")
    @GetMapping("/{id}/inventory")
    public ResponseEntity<List<InventoryItem>> getInventoryItems(@PathVariable("id") long householdId) throws HouseholdNotFoundException {
        logger.info("Getting inventory items for household with id: " + householdId);
        Household household = householdService.findHouseholdByHouseholdId(householdId);

        List<Item> items = itemService.getHouseholdItems(household);

        ArrayList<InventoryItem> inventoryItems = new ArrayList<>();
        for (Item item : items) {
            inventoryItems.add(itemMapper.toItemDto(item));
        }
        logger.info("Returning household inventory items");
        return ResponseEntity.ok(inventoryItems);
    }

    /**
     * Deletes an inventory item, checks as well that the resources exist and that the item belongs to the household
     * @param householdId The id of the household to delete the item from
     * @param itemId The id of the item to delete
     * @return A message indicating that the item was deleted
     * @throws HouseholdNotFoundException If the household is not found
     * @throws ItemNotFoundException If the item is not found
     * @throws ItemDoesNotBelongToHouseholdException If the item does not belong to the household
     */
    @PreAuthorize("hasPermission(#householdId, 'Household', 'SUPERUSER')")
    @DeleteMapping("/{id}/inventory/{itemId}")
    public ResponseEntity<String> deleteInventoryItem(@PathVariable("id") long householdId, @PathVariable("itemId") long itemId) throws HouseholdNotFoundException, ItemNotFoundException, ItemDoesNotBelongToHouseholdException {
        logger.info("Deleting inventory item with id: " + itemId);
        Household household = householdService.findHouseholdByHouseholdId(householdId);
        Item item = itemService.getItemByIdAndHousehold(itemId, household);

        itemService.deleteItemById(item.getId());
        logger.info("Deleted inventory item with id: " + itemId);

        return ResponseEntity.ok("Deleted inventory item with id: " + itemId);
    }

    /**
     * Updates an inventory item, checks as well that the resources exist and that the item belongs to the household
     * @param householdId The id of the household to update the item in
     * @param requestBody An object containing the id of the item to update, the new remaining amount and the new state
     * @return A message indicating that the item was updated
     * @throws HouseholdNotFoundException If the household is not found
     * @throws ItemNotFoundException If the item is not found
     * @throws ItemDoesNotBelongToHouseholdException If the item does not belong to the household
     * @throws IllegalItemStatusException If the item status is invalid
     * @throws IllegalItemParameterException If the item "remaining" field value is higher than 1 or lower than 0
     */
    @PreAuthorize("hasPermission(#householdId, 'Household', 'SUPERUSER')")
    @PutMapping("/{id}/inventory")
    public ResponseEntity<String> updateInventoryItem(@PathVariable("id") long householdId, @RequestBody Map<String, Object> requestBody) throws HouseholdNotFoundException, IllegalItemStatusException, ItemDoesNotBelongToHouseholdException, IllegalItemParameterException, ItemNotFoundException {
        logger.info("Updating inventory item with id: " + requestBody.get("id"));
        long itemId = Long.parseLong(requestBody.get("itemId").toString());
        double remaining = Float.parseFloat(requestBody.get("remaining").toString());
        String state = requestBody.get("state").toString();

        try {
            ItemStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalItemStatusException();
        }

        Household household = householdService.findHouseholdByHouseholdId(householdId);
        Item item = itemService.getItemByIdAndHousehold(itemId, household);

        item.setRemaining(remaining);
        item.setStatus(ItemStatus.valueOf(state));
        itemService.updateItem(item);

        logger.info("Updated inventory item with id: " + itemId);
        return ResponseEntity.ok("Updated inventory item with id: " + itemId);
    }

    /**
     * Adds inventory item suggestion to the inventory of a given household
     * @param householdId The id of the household
     * @param requestBody A list of objects each containing the itemTypeId and count of the item to suggest
     * @return A list of the added inventory suggestions
     * @throws UserNotFoundException If the user is not found
     * @throws HouseholdNotFoundException If the household is not found
     * @throws ItemTypeNotFoundException If the item type is not found
     */
    @PreAuthorize("hasPermission(#householdId, 'Household', '')")
    @PostMapping("/{id}/inventory/suggest")
    public ResponseEntity<List<InventoryItem>> addInventorySuggestion(@PathVariable("id") long householdId, @RequestBody List<Map<String, Object>> requestBody, @AuthenticationPrincipal UserDetailsImpl userDetails) throws UserNotFoundException, HouseholdNotFoundException, ItemTypeNotFoundException {
        User user = userService.getUserById(userDetails.getId());

        logger.info("Adding suggestions to inventory of household with id: " + householdId);

        Household household = householdService.findHouseholdByHouseholdId(householdId);

        ArrayList<InventoryItem> inventoryItems = new ArrayList<>();
        for (Map<String, Object> item : requestBody) {
            long itemTypeId = Long.parseLong(item.get("itemTypeId").toString());
            int count = (int) item.get("count");

            for (int i = 0; i < count; i++) {
                Item newItem = new Item();
                newItem.setSuggested(true);
                newItem.setHousehold(household);
                newItem.setAddedBy(user);
                newItem.setType(itemTypeService.getItemTypeById(itemTypeId));
                newItem.setStatus(ItemStatus.INVENTORY);

                itemService.addItem(newItem);
                inventoryItems.add(itemMapper.toItemDto(newItem));
            }
        }

        logger.info("Added suggestion to inventory");

        return ResponseEntity.ok(inventoryItems);
    }
}
