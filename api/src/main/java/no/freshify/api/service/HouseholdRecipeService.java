package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.HouseholdNotFoundException;
import no.freshify.api.exception.HouseholdRecipeNotFoundException;
import no.freshify.api.model.recipe.HouseholdRecipe;
import no.freshify.api.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseholdRecipeService {
    private final HouseholdRecipeRepository householdRecipeRepository;

    private final Logger logger = LoggerFactory.getLogger(HouseholdService.class);
    private final HouseholdRepository householdRepository;

    public HouseholdRecipe addRecipe(HouseholdRecipe householdRecipe) {
        return householdRecipeRepository.save(householdRecipe);
    }

    public HouseholdRecipe getRecipe(Long householdId, Long recipeId) {
        return householdRecipeRepository.findByHouseholdIdAndRecipeId(householdId, recipeId);
    }

    public List<HouseholdRecipe> getRecipes(Long householdId) throws HouseholdNotFoundException {
        if (!householdRepository.existsById(householdId)) {
            logger.warn("Household with id " + householdId + " does not exist");
            throw new HouseholdNotFoundException();
        }

        return householdRecipeRepository.findAllByHouseholdId(householdId);
    }

    public void removeHouseholdRecipe(Long householdId, Long recipeId) throws HouseholdRecipeNotFoundException {
        HouseholdRecipe householdRecipe = getRecipe(householdId, recipeId);

        if (householdRecipe == null) {
            logger.warn("HouseholdRecipe with householdId " + householdId + " and recipeId " + recipeId + " does not exist");
            throw new HouseholdRecipeNotFoundException();
        }

        householdRecipeRepository.delete(householdRecipe);
    }
}
