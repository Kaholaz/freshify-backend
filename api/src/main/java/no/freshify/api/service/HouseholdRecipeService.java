package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.model.recipe.HouseholdRecipe;
import no.freshify.api.model.recipe.Recipe;
import no.freshify.api.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HouseholdRecipeService {

    private final HouseholdMemberService householdMemberService;

    private final HouseholdRepository householdRepository;
    private final RecipeRepository recipeRepository;
    private final HouseholdRecipeRepository householdRecipeRepository;
    private final HouseholdMemberRepository householdMemberRepository;
    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(HouseholdService.class);

    public HouseholdRecipe addRecipe(HouseholdRecipe householdRecipe) {
        return householdRecipeRepository.save(householdRecipe);
    }
}
