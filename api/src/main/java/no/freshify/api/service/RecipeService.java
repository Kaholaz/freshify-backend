package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.model.recipe.Allergen;
import no.freshify.api.model.recipe.Recipe;
import no.freshify.api.repository.RecipeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final RecipeIngredientService recipeIngredientService;

    public Page<Recipe> getRecipesByCategoryPageable(Long categoryId, Pageable pageable) {
        return recipeRepository.findByCategoriesId(categoryId, pageable);
    }

    public Page<Recipe> getRecipesPageable(Pageable pageable) {
        return recipeRepository.findAll(pageable);
    }

    public Recipe addRecipe(Recipe recipe) {
        try {
            recipeIngredientService.addRecipeIngredients(recipe.getRecipeIngredients());
            return recipeRepository.save(recipe);
        } catch (Exception e) {
            throw new RuntimeException("Could not add recipe");
        }
    }

    public Page<Recipe> getRecipesNotContainingAllergensPageable(List<Allergen> allergens, Pageable pageable) {
        return recipeRepository.findAllNotContainingAllergens(allergens, pageable);
    }

    public Page<Recipe> getRecipesByCategoryAndNotContainingAllergensPageable(Long categoryId, List<Allergen> allergens, Pageable pageable) {
        Set<Allergen> allergensSet = new HashSet<>(allergens);
        return recipeRepository.findByCategoriesIdAndAllergensNotInOrNoAllergens(categoryId, allergensSet, pageable);
    }

}