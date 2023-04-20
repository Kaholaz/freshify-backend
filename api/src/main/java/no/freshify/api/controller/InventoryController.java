package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.HouseholdNotFoundException;
import no.freshify.api.exception.ItemTypeNotFoundException;
import no.freshify.api.exception.UserNotFoundException;
import no.freshify.api.model.Household;
import no.freshify.api.model.Item;
import no.freshify.api.model.ItemStatus;
import no.freshify.api.model.User;
import no.freshify.api.model.dto.InventoryItem;
import no.freshify.api.service.HouseholdService;
import no.freshify.api.service.ItemService;
import no.freshify.api.service.ItemTypeService;
import no.freshify.api.service.UserService;
import org.springframework.http.ResponseEntity;
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

    //TODO Remember to add authentication logic and also verify correct access privileges for resources before processing request
    //TODO Once authentication is in place, extract user from jwttoken and set addedBy field of the item to the given user
    @PostMapping("/{id}/inventory")
    public ResponseEntity<List<InventoryItem>> addInventoryItem(@PathVariable("id") long householdId, @RequestBody List<Map<String, Object>> requestBody) throws UserNotFoundException, HouseholdNotFoundException, ItemTypeNotFoundException {
        User user = userService.getUserById(1L);//TODO: Get user from token, using default id 1 for now
        if (user == null) {
            throw new UserNotFoundException();
        }

        Household household = householdService.findHouseholdByHouseholdId(householdId);
        if (household == null) {
            throw new HouseholdNotFoundException();
        }

        ArrayList<InventoryItem> inventoryItems = new ArrayList<>();
        for (Map<String, Object> item : requestBody) {
            long itemTypeId = Long.parseLong(item.get("itemTypeId").toString());
            int count = (int) item.get("count");

            for (int i = 0; i < count; i++) {
                Item newItem = new Item();
                newItem.setHousehold(household);
                newItem.setAddedBy(user);
                newItem.setType(itemTypeService.getItemTypeById(itemTypeId));
                newItem.setStatus(ItemStatus.INVENTORY);

                itemService.addItem(newItem);
                inventoryItems.add(new InventoryItem(newItem));
            }
        }

        return ResponseEntity.ok(inventoryItems);
    }
}
