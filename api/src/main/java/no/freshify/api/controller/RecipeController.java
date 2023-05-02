package no.freshify.api.controller;


import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.AllergenNotFoundException;
import no.freshify.api.exception.ItemTypeNotFoundException;
import no.freshify.api.exception.RecipeCategoryNotFoundException;
import no.freshify.api.model.dto.AllergenDTO;
import no.freshify.api.model.dto.RecipeDTO;
import no.freshify.api.model.dto.RecipeRequest;
import no.freshify.api.model.mapper.RecipeMapper;
import no.freshify.api.model.recipe.Allergen;
import no.freshify.api.model.recipe.Recipe;
import no.freshify.api.model.recipe.RecipeCategory;
import no.freshify.api.model.recipe.RecipeIngredient;
import no.freshify.api.service.AllergenService;
import no.freshify.api.service.ItemTypeService;
import no.freshify.api.service.RecipeCategoryService;
import no.freshify.api.service.RecipeService;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final ItemTypeService itemTypeService;
    private final AllergenService allergenService;
    private final RecipeCategoryService recipeCategoryService;

    private final RecipeMapper recipeMapper = Mappers.getMapper(RecipeMapper.class);

    Logger logger = LoggerFactory.getLogger(RecipeController.class);
    /**
     * API endpoint for getting paginated list of recipes, can be filtered by category and allergens.
     * @param categoryId The category id to filter by
     * @param allergenIds The list of allergens to exclude from the search results
     * @param pageNo The page number
     * @param pageSize The page size
     * @return A page of recipes
     */
    @GetMapping("/category")
    public Page<RecipeDTO> getRecipesPaginated(@RequestParam(required = false) Long categoryId,
                                               @RequestParam(required = false) List<Long> allergenIds,
                                               @RequestParam(defaultValue = "0") int pageNo,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("name"));

        Page<Recipe> recipePage;

        if (categoryId != null && allergenIds != null) {
            List<Allergen> allergens = allergenService.getAllergensByIds(allergenIds);
            recipePage = recipeService.getRecipesByCategoryAndNotContainingAllergensPageable(categoryId, allergens, pageable);
        } else if (categoryId != null) {
            recipePage = recipeService.getRecipesByCategoryPageable(categoryId, pageable);
        } else if (allergenIds != null) {
            List<Allergen> allergenList = allergenService.getAllergensByIds(allergenIds);
            recipePage = recipeService.getRecipesNotContainingAllergensPageable(allergenList, pageable);
        } else {
            recipePage = recipeService.getRecipesPageable(pageable);
        }

        return recipeMapper.toRecipeDTOPage(recipePage);
    }

    /**
     * API endpoint for creating new recipes
     * @param recipe The recipe request object to create.
     * @return The created recipe object.
     */
    @PostMapping
    public ResponseEntity<RecipeDTO> createRecipe(@RequestBody RecipeRequest recipe) throws ItemTypeNotFoundException, AllergenNotFoundException, RecipeCategoryNotFoundException {
        Recipe recipeToAdd = recipeMapper.toRecipe(recipe);
        logger.info("Recipe to add: " + recipeToAdd.toString());

        for (Allergen allergen : recipeToAdd.getAllergens()) {
            allergenService.getAllergenById(allergen.getId());
        }
        logger.info("Allergens found");

        for (RecipeCategory category : recipeToAdd.getCategories()) {
            recipeCategoryService.getRecipeCategoryById(category.getId());
        }
        logger.info("Categories found");

        for (RecipeIngredient ingredient : recipeToAdd.getRecipeIngredients()) {
            ingredient.setItemType(itemTypeService.getItemTypeById(ingredient.getItemType().getId()));
            ingredient.setRecipe(recipeToAdd);
        }

        RecipeDTO createdRecipe = recipeMapper.toRecipeDTO(recipeService.addRecipe(recipeToAdd));

        logger.info("Returning created recipe");
        return ResponseEntity.ok(createdRecipe);
    }
}