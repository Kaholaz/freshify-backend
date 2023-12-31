package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.*;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.*;
import no.freshify.api.model.mapper.ItemMapper;
import no.freshify.api.model.mapper.ItemMapperImpl;
import no.freshify.api.security.AuthenticationService;
import no.freshify.api.security.UserDetailsImpl;
import no.freshify.api.service.HouseholdService;
import no.freshify.api.service.ItemService;
import no.freshify.api.service.ItemTypeService;
import no.freshify.api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.time.LocalDate;
import java.util.*;

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

        List<Item> items = itemService.getInventoryItems(household);
        List<InventoryItem> inventoryItems = itemMapper.toItemDtos(items);

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
    public ResponseEntity<String> updateInventoryItem(
            @PathVariable("id") long householdId,
            @RequestBody UpdateInventoryItem requestBody
    ) throws HouseholdNotFoundException, ItemDoesNotBelongToHouseholdException, IllegalItemParameterException, ItemNotFoundException, IllegalItemStatusException {
        logger.info("Updating inventory item with id: " + requestBody.getItemId());
        if(requestBody.getState() == null) return ResponseEntity.badRequest().body("Item state cannot be null");

        Item newItem = itemMapper.toItem(requestBody);

        Household household = householdService.findHouseholdByHouseholdId(householdId);
        Item item = itemService.getItemByIdAndHousehold(newItem.getId(), household);

        item.setRemaining(newItem.getRemaining());
        item.setStatus(newItem.getStatus());
        item.setLastChanged(new Date());
        itemService.updateItem(item);

        logger.info("Updated inventory item with id: " + item.getId());
        return ResponseEntity.ok("Updated inventory item with id: " + item.getId());
    }

    /**
     * Gets the item waste for a household as a list.
     * @param householdId The id of the household to get the item waste from
     * @param numMonths The number of months to get the item waste from
     * @return A list of inventory items with their waste
     * @throws HouseholdNotFoundException If the household is not found
     */
    @PreAuthorize("hasPermission(#householdId, 'Household', '')")
    @GetMapping("/{id}/inventory/waste")
    public ResponseEntity<InventoryWasteResponse> getInventoryWaste(@PathVariable("id") long householdId,
                                                                    @RequestParam(value = "limit", required = true) int numMonths)
            throws HouseholdNotFoundException {
        logger.info("Getting inventory item waste for household with id: " + householdId);
        Household household = householdService.findHouseholdByHouseholdId(householdId);

        LocalDate startDate = LocalDate.now().minusMonths(numMonths);

        List<Item> wastedItems = itemService.findAllUsedItemsInTimeInterval
                (household, java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(LocalDate.now().plusDays(1)));

        Double average = itemService.getAverageWaste(wastedItems);

        List<WastedItemDTO> wastedItemDTOS = itemMapper.toWastedItemDTO(wastedItems);

        InventoryWasteResponse response = new InventoryWasteResponse(wastedItemDTOS, wastedItems.size(), average);

        return ResponseEntity.ok(response);
    }

    /**
     * Gets the average item waste per amount for a household per month for a given amount of months back in time.
     * example output:
     * {0.23, 0.12, 0.0, 0.7}
     * means: average wastage this month: 0.23, previous month: 0.12, etc..
     * @param householdId The id of the household to get the item waste from
     * @param numMonths The number of months back to get the item waste from
     * @return A list of doubles with the average item waste per amount
     * @throws HouseholdNotFoundException If the household is not found
     */
    @PreAuthorize("hasPermission(#householdId, 'Household', '')")
    @GetMapping("/{id}/inventory/waste-per-month")
    public ResponseEntity<List<Double>> getInventoryWastePerMonth(@PathVariable("id") long householdId,
                                                                  @RequestParam(value = "limit", required = true) Integer numMonths)
            throws HouseholdNotFoundException {
        logger.info("Getting inventory item waste for household with id: " + householdId);

        Household household = householdService.findHouseholdByHouseholdId(householdId);

        // Items with state USED also include wasted items
        List<Item> usedItems = itemService.findAllUsedItems(household);

        // Map<monthsFromNow, Pair<numItems, totalRemaining>>
        Map<Integer, Pair<Integer, Double>> itemMap = new HashMap<>();

        LocalDate now = LocalDate.now();
        Calendar calendar = Calendar.getInstance();
        for (Item item : usedItems) {
            java.util.Date usedDate = item.getLastChanged();
            calendar.setTime(usedDate);

            double remaining = item.getRemaining();
            int yearUsed = calendar.get(Calendar.YEAR);
            int monthUsed = calendar.get(Calendar.MONTH) + 1;
            int monthsFromNow = (now.getYear() - yearUsed) * 12 + (now.getMonthValue() - monthUsed);

            if (monthsFromNow < numMonths) {
                Pair<Integer, Double> pair = itemMap.get(monthsFromNow);
                if (pair == null) {
                    itemMap.put(monthsFromNow, Pair.of(1, remaining));
                } else {
                    itemMap.put(monthsFromNow, Pair.of(pair.getFirst() + 1, pair.getSecond() + remaining));
                }
            }
        }

        // Calculate averages for each month
        List<Double> monthAverages = new ArrayList<>(Collections.nCopies(numMonths, 0D));

        for (Map.Entry<Integer, Pair<Integer, Double>> entry : itemMap.entrySet()) {
            monthAverages.set(entry.getKey(), entry.getValue().getSecond() / entry.getValue().getFirst());
        }

        return ResponseEntity.ok(monthAverages);
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
