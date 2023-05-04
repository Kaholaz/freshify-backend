package no.freshify.api.controller;


import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.*;
import no.freshify.api.model.Household;
import no.freshify.api.model.Item;
import no.freshify.api.model.ItemType;
import no.freshify.api.model.RecipeFilter;
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
     * @param categoryIds The list of category ids to filter by
     * @param allergenIds The list of allergen ids to exclude from the search results
     * @param sortByIngredientsInFridge Whether to sort recipes in descending order by how many of the recipe ingredients are in fridge
     * @param searchString The search string to filter the recipes by, searches both name and description.
     * @param getHouseholdRecipes Whether to include household recipes in the search results.
     *                            When true, returns only recipes that are in household
     *                            When false, returns only recipes that aren't in household
     *                            When not passed/null, returns all recipes
     * @param pageNo The page number
     * @param pageSize The page size
     * @return A page of recipes
     */
    @PreAuthorize("hasPermission(#householdId, 'HOUSEHOLD', '')")
    @GetMapping("/{householdId}")
    public Page<RecipeDTO> getRecipesPaginated(@PathVariable("householdId") Long householdId,
                                               @RequestParam(required = false) boolean sortByIngredientsInFridge,
                                               @RequestParam(required = false) String searchString,
                                               @RequestParam(required = false) List<Long> categoryIds,
                                               @RequestParam(required = false) List<Long> allergenIds,
                                               @RequestParam(required = false) Boolean getHouseholdRecipes,
                                               @RequestParam(defaultValue = "0") int pageNo,
                                               @RequestParam(defaultValue = "10") int pageSize) throws HouseholdNotFoundException {
        logger.info("Searching for recipes with name: " + searchString + ", category id: " + categoryIds + ", allergen ids: " + allergenIds + ", page number: " + pageNo + ", page size: " + pageSize);

        Household household = householdService.findHouseholdByHouseholdId(householdId);

        RecipeFilter.RecipeFilterBuilder filterBuilder = RecipeFilter.builder();
        filterBuilder
                .searchString(searchString)
                .categoryIds(categoryIds)
                .allergenIds(allergenIds)
                .householdId(householdId)
                .sortByIngredientsInFridge(sortByIngredientsInFridge)
                .getHouseholdRecipes(getHouseholdRecipes);

        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<Recipe> recipes = recipeService.getRecipesByFilter(filterBuilder.build(), pageRequest);

        Page<RecipeDTO> recipeDTOPage = recipeMapper.toRecipeDTOPage(recipes);
        for (RecipeDTO recipeDTO : recipeDTOPage.getContent()) {
            checkIngredientsInHousehold(household, recipeDTO);
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
                List<Item> items = itemService.findByTypeAndHousehold(itemType, household);
                if (items == null || items.isEmpty()) {
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