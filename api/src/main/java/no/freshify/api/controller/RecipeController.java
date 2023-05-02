package no.freshify.api.controller;


import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.*;
import no.freshify.api.model.Household;
import no.freshify.api.model.Item;
import no.freshify.api.model.ItemType;
import no.freshify.api.model.dto.AllergenDTO;
import no.freshify.api.model.dto.RecipeDTO;
import no.freshify.api.model.dto.RecipeIngredientDTO;
import no.freshify.api.model.dto.RecipeRequest;
import no.freshify.api.model.mapper.RecipeMapper;
import no.freshify.api.model.recipe.Allergen;
import no.freshify.api.model.recipe.Recipe;
import no.freshify.api.model.recipe.RecipeCategory;
import no.freshify.api.model.recipe.RecipeIngredient;
import no.freshify.api.security.UserDetailsImpl;
import no.freshify.api.service.*;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final ItemTypeService itemTypeService;
    private final AllergenService allergenService;
    private final RecipeCategoryService recipeCategoryService;
    private final ItemService itemService;
    private final HouseholdService householdService;

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
    @PreAuthorize("hasPermission(#householdId, 'HOUSEHOLD', '')")
    @GetMapping("/{householdId}")
    public Page<RecipeDTO> getRecipesPaginated(@PathVariable("householdId") Long householdId,
                                               @RequestParam(required = false) Long categoryId,
                                               @RequestParam(required = false) List<Long> allergenIds,
                                               @RequestParam(defaultValue = "0") int pageNo,
                                               @RequestParam(defaultValue = "10") int pageSize) throws HouseholdNotFoundException {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("name"));
        Household household = householdService.findHouseholdByHouseholdId(householdId);

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

        Page<RecipeDTO> recipeDTOPage = recipeMapper.toRecipeDTOPage(recipePage);

        for (RecipeDTO recipe : recipeDTOPage.getContent()) {
            checkIngredientsInHousehold(household , recipe);
        }

        return recipeDTOPage;
    }

    /**
     * API endpoint for getting a single recipe by id.
     * @param id The id of the recipe to get.
     * @return The recipe object.
     */
    @GetMapping("/{householdId}/recipe/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable("householdId") Long householdId, @PathVariable("id") Long id) throws RecipeNotFoundException, HouseholdNotFoundException {
        Recipe recipe = recipeService.getRecipeById(id);

        Household household = householdService.findHouseholdByHouseholdId(householdId);

        RecipeDTO recipeDTO = recipeMapper.toRecipeDTO(recipe);

        checkIngredientsInHousehold(household, recipeDTO);

        return ResponseEntity.ok(recipeDTO);
    }

    /**
     * Helper method for checking and setting if a household's fridge has given ingredients.
     * @param household The household to check against.
     * @param recipeDTO The recipe to check.
     */
    private void checkIngredientsInHousehold(Household household , RecipeDTO recipeDTO) {
        for (RecipeIngredientDTO recipeIngredient : recipeDTO.getRecipeIngredients()) {
            ItemType itemType = recipeIngredient.getItemType();
            if (itemType != null) {
                Item item = itemService.findByTypeAndHousehold(itemType, household);
                if (item == null) {
                    recipeIngredient.setHouseholdHasIngredient(false);
                } else {
                    recipeIngredient.setHouseholdHasIngredient(true);
                    recipeDTO.setTotalIngredientsInFridge(recipeDTO.getTotalIngredientsInFridge() + 1);
                }
            }
        }
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