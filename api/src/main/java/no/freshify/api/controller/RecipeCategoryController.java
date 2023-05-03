package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.model.dto.RecipeCategoryDTO;
import no.freshify.api.model.mapper.RecipeMapper;
import no.freshify.api.service.RecipeCategoryService;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/recipecategory")
@RequiredArgsConstructor
@RestController
public class RecipeCategoryController {
    public final RecipeCategoryService recipeCategoryService;
    public final RecipeMapper recipeMapper = Mappers.getMapper(RecipeMapper.class);

    /**
     * Gets all recipe categories
     * @return a list of all recipe categories
     */
    @GetMapping
    public ResponseEntity<List<RecipeCategoryDTO>> getAllRecipeCategories() {
        return ResponseEntity.ok(recipeMapper.recipeCategoryDTO(recipeCategoryService.getAllRecipeCategories()));
    }
}
