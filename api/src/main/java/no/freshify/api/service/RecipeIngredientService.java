package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.model.Household;
import no.freshify.api.model.ShoppingListEntry;
import no.freshify.api.model.recipe.RecipeIngredient;
import no.freshify.api.repository.RecipeIngredientRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecipeIngredientService {
    private final RecipeIngredientRepository recipeIngredientRepository;

    public void addRecipeIngredients(Set<RecipeIngredient> recipeIngredients) {
        try {
            recipeIngredientRepository.saveAll(recipeIngredients);
        } catch (Exception e) {
            throw new RuntimeException("Could not add recipe ingredients");
        }
    }
}
