package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.HouseholdNotFoundException;
import no.freshify.api.exception.RecipeNotFoundException;
import no.freshify.api.model.Household;
import no.freshify.api.model.dto.HouseholdRecipeDTO;
import no.freshify.api.model.mapper.HouseholdMapper;
import no.freshify.api.model.mapper.HouseholdMemberMapper;
import no.freshify.api.model.mapper.HouseholdRecipeMapper;
import no.freshify.api.model.recipe.HouseholdRecipe;
import no.freshify.api.model.recipe.Recipe;
import no.freshify.api.security.AuthenticationService;
import no.freshify.api.service.HouseholdMemberService;
import no.freshify.api.service.HouseholdRecipeService;
import no.freshify.api.service.HouseholdService;
import no.freshify.api.service.RecipeService;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/householdrecipe/{householdId}")
@RequiredArgsConstructor
@RestController
public class HouseholdRecipeController {
    private final HouseholdService householdService;
    private final RecipeService recipeService;
    private final AuthenticationService authenticationService;
    private final HouseholdMemberService householdMemberService;
    private final HouseholdRecipeService householdRecipeService;

    private final HouseholdMapper householdMapper = Mappers.getMapper(HouseholdMapper.class);
    private final HouseholdRecipeMapper householdRecipeMapper = Mappers.getMapper(HouseholdRecipeMapper.class);
    private final HouseholdMemberMapper householdMemberMapper = Mappers.getMapper(HouseholdMemberMapper.class);
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
        householdRecipeService.addRecipe(householdRecipe);

        return ResponseEntity.status(HttpStatus.CREATED).body(householdRecipeMapper.toHouseholdRecipeDTO(householdRecipe));
    }

    /**
     * Gets all bookmarked recipes in a household
     * @param householdId The id of the household
     * @return A list of all bookmarked recipes in the household
     */
    @PreAuthorize("hasPermission(#householdId, 'Household', '')")
    @GetMapping("/recipe")
    public ResponseEntity<List<HouseholdRecipeDTO>> getHouseholdRecipes(@PathVariable("householdId") Long householdId) {
        logger.info("Getting all bookmarked recipes in household");

        List<HouseholdRecipe> recipes = householdRecipeService.getRecipes(householdId);
        List<HouseholdRecipeDTO> recipeDTOS = householdRecipeMapper.toHouseholdRecipeDTOs(recipes);

        return ResponseEntity.ok(recipeDTOS);
    }
}
