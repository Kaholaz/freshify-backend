package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
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

    public HouseholdRecipe addRecipe(HouseholdRecipe householdRecipe) {
        return householdRecipeRepository.save(householdRecipe);
    }

    public List<HouseholdRecipe> getRecipes(Long householdId) {
        return householdRecipeRepository.findAllByHouseholdId(householdId);
    }
}
