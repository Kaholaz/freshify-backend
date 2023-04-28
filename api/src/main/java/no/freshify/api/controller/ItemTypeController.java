package no.freshify.api.controller;


import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.ItemTypesNotFoundException;
import no.freshify.api.model.dto.ItemTypeDTO;
import no.freshify.api.model.mapper.ItemMapper;
import no.freshify.api.model.mapper.ItemMapperImpl;
import no.freshify.api.service.ItemTypeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
     * Search for item types by name
     * @param name the name of the item type
     * @return A list of items matching the name
     */
    @GetMapping
    public ResponseEntity<List<ItemTypeDTO>> searchItemTypes(@RequestParam String name) throws ItemTypesNotFoundException {
        logger.info("Searching for item types with name: " + name);
        return ResponseEntity.ok(itemMapper.toItemTypeDTO(itemTypeService.searchItemTypes(name)));
    }

    //TODO explore the possibility of implementing user admins that can add new or change existing item types
}
