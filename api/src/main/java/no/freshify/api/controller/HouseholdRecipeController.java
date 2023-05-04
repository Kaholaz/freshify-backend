package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.HouseholdNotFoundException;
import no.freshify.api.exception.HouseholdRecipeNotFoundException;
import no.freshify.api.exception.RecipeNotFoundException;
import no.freshify.api.exception.ShoppingListEntryAlreadyExistsException;
import no.freshify.api.model.Household;
import no.freshify.api.model.ItemType;
import no.freshify.api.model.ShoppingListEntry;
import no.freshify.api.model.dto.HouseholdRecipeDTO;
import no.freshify.api.model.dto.RecipeIngredientDTO;
import no.freshify.api.model.mapper.HouseholdRecipeMapper;
import no.freshify.api.model.mapper.RecipeMapper;
import no.freshify.api.model.recipe.HouseholdRecipe;
import no.freshify.api.model.recipe.Recipe;
import no.freshify.api.model.recipe.RecipeIngredient;
import no.freshify.api.security.AuthenticationService;
import no.freshify.api.service.*;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RequestMapping("/householdrecipe/{householdId}")
@RequiredArgsConstructor
@RestController
public class HouseholdRecipeController {
    private final HouseholdService householdService;
    private final AuthenticationService authenticationService;
    private final ShoppingListEntryService shoppingListEntryService;
    private final ItemService itemService;
    private final RecipeService recipeService;
    private final RecipeIngredientService recipeIngredientService;
    private final HouseholdRecipeService householdRecipeService;

    private final HouseholdRecipeMapper householdRecipeMapper = Mappers.getMapper(HouseholdRecipeMapper.class);
    private final RecipeMapper recipeMapper = Mappers.getMapper(RecipeMapper.class);
    private final Logger logger = LoggerFactory.getLogger(HouseholdMemberController.class);


    /**
     * Creates a household recipe / bookmarks a recipe in a household
     * @param householdId The id of the household
     * @param id The id of the recipe
     * @return The created household recipe
     * @throws HouseholdNotFoundException If the household is not found
     * @throws RecipeNotFoundException If the recipe is not found
     */
    @PreAuthorize("hasPermission(#householdId, 'Household', '')")
    @PostMapping("/recipe/{id}")
    public ResponseEntity<HouseholdRecipeDTO> createHouseholdRecipe(@PathVariable("householdId") Long householdId,
                                                                    @PathVariable("id") Long id)
            throws HouseholdNotFoundException, RecipeNotFoundException {
        logger.info("Creating household recipe / bookmarking recipe in household");
        Household household = householdService.findHouseholdByHouseholdId(householdId);
        Recipe recipe = recipeService.getRecipeById(id);

        HouseholdRecipe householdRecipe = new HouseholdRecipe();
        householdRecipe.setHousehold(household);
        householdRecipe.setRecipe(recipe);
        HouseholdRecipe result = householdRecipeService.addRecipe(householdRecipe);

        return ResponseEntity.status(HttpStatus.CREATED).body(householdRecipeMapper.toHouseholdRecipeDTO(result));
    }

    /**
     * Gets all bookmarked recipes in a household
     * @param householdId The id of the household
     * @return A list of all bookmarked recipes in the household
     * @throws HouseholdNotFoundException If the household is not found
     */
    @PreAuthorize("hasPermission(#householdId, 'Household', '')")
    @GetMapping("/recipe")
    public ResponseEntity<List<HouseholdRecipeDTO>> getHouseholdRecipes(@PathVariable("householdId") Long householdId) throws HouseholdNotFoundException {
        logger.info("Getting all bookmarked recipes in household");

        List<HouseholdRecipe> recipes = householdRecipeService.getRecipes(householdId);
        List<HouseholdRecipeDTO> recipeDTOS = householdRecipeMapper.toHouseholdRecipeDTOs(recipes);

        return ResponseEntity.ok(recipeDTOS);
    }

    /**
     * Removes a household recipe / bookmarked recipe in a household. Can only be done by superuser
     * @param householdId The id of the household
     * @param id The id of the recipe
     * @return NO_CONTENT if the household recipe was removed
     * @throws HouseholdRecipeNotFoundException If the household recipe is not found
     */
    @PreAuthorize("hasPermission(#householdId, 'Household', 'SUPERUSER')")
    @DeleteMapping("/recipe/{id}")
    public ResponseEntity<HttpStatus> removeHouseholdRecipe(@PathVariable("householdId") Long householdId,
                                                            @PathVariable("id") Long id) throws HouseholdRecipeNotFoundException {
        logger.info("Removing household recipe / bookmarked recipe in household");

        householdRecipeService.removeHouseholdRecipe(householdId, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Adds all missing ingredients from a recipe to the shopping list
     * @param householdId The id of the household
     * @param id The id of the recipe
     * @return OK if the ingredients were added to the shopping list, body is a list of added ingredients
     * @throws HouseholdRecipeNotFoundException If the household recipe is not found
     * @throws RecipeNotFoundException If the recipe is not found
     * @throws HouseholdNotFoundException If the household is not found
     */
    @PreAuthorize("hasPermission(#householdId, 'Household', 'SUPERUSER')")
    @PostMapping("/recipe/{id}/add-to-shoppinglist")
    public ResponseEntity<List<RecipeIngredientDTO>> addRecipeToShoppingList(@PathVariable("householdId") Long householdId,
                                                                             @PathVariable("id") Long id)
            throws RecipeNotFoundException, HouseholdNotFoundException {
        logger.info("Adding recipe's missing items to shopping list");

        Household household = householdService.findHouseholdByHouseholdId(householdId);
        Recipe recipe = recipeService.getRecipeById(id);


        List<RecipeIngredient> missingIngredients = householdRecipeService.getMissingIngredients(household, recipe);

        List<ShoppingListEntry> shoppingListEntries = shoppingListEntryService.getShoppingList(householdId);
        Set<ItemType> uniqueShoppingListItemTypes = shoppingListEntryService.getUniqueItemTypes(shoppingListEntries);

        // Filter out missing ingredients that are already in the shopping list
        // Avoids adding items that are already in the shopping list
        missingIngredients = missingIngredients.stream()
                .filter(e -> !uniqueShoppingListItemTypes.contains(e.getItemType()))
                .toList();

        missingIngredients.forEach(recipeIngredient -> {
            ShoppingListEntry shoppingListEntry = new ShoppingListEntry();
            shoppingListEntry.setType(recipeIngredient.getItemType());
            shoppingListEntry.setHousehold(household);
            shoppingListEntry.setAddedBy(authenticationService.getLoggedInUser());
            shoppingListEntry.setSuggested(false);
            shoppingListEntry.setChecked(false);
            shoppingListEntry.setCount(1L);

            try {
                shoppingListEntryService.addItem(shoppingListEntry);
            } catch (ShoppingListEntryAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
        });

        return ResponseEntity.ok(recipeMapper.recipeIngredientDTO(missingIngredients));
    }
}
