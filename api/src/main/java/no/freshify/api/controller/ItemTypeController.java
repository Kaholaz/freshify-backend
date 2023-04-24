package no.freshify.api.controller;


import lombok.RequiredArgsConstructor;
import no.freshify.api.model.ItemType;
import no.freshify.api.model.mapper.ItemMapper;
import no.freshify.api.model.mapper.ItemMapperImpl;
import no.freshify.api.service.ItemTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemtype")
@RequiredArgsConstructor
public class ItemTypeController {
    private final ItemTypeService itemTypeService;
    private final ItemMapper itemMapper = new ItemMapperImpl();

    Logger logger = LoggerFactory.getLogger(ItemTypeController.class);

    /**
     * Gets all item types
     * @return A list of all item types
     */
    @GetMapping
    public ResponseEntity<List<ItemType>> getAllItemTypes() {
        logger.info("Fetching all item types");
        return ResponseEntity.ok(itemMapper.toItemTypeDTO(itemTypeService.getAllItemTypes()));
    }

    //TODO explore the possibility of implementing user admins that can add new or change existing item types
}
