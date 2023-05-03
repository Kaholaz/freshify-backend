package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.model.dto.AllergenDTO;
import no.freshify.api.model.mapper.AllergenMapper;
import no.freshify.api.service.AllergenService;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/allergens")
@RequiredArgsConstructor
@RestController
public class AllergenController {
    private final AllergenService allergenService;
    private final AllergenMapper allergenMapper = Mappers.getMapper(AllergenMapper.class);

    /**
     * Gets all allergens
     * @return a list of all allergens
     */
    @GetMapping
    public ResponseEntity<List<AllergenDTO>> getAllergens() {
        return ResponseEntity.ok(allergenMapper.toAllergenDTO(allergenService.getAllergens()));
    }
}
