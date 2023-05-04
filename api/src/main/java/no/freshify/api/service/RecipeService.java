package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.RecipeNotFoundException;
import no.freshify.api.model.Household;
import no.freshify.api.model.recipe.Allergen;
import no.freshify.api.model.recipe.Recipe;
import no.freshify.api.repository.RecipeRepository;
import org.hibernate.service.spi.InjectService;
import org.springframework.beans.factory.annotation.Autowired;
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
            System.out.println(e.getMessage());
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

    public Recipe getRecipeById(Long id) throws RecipeNotFoundException {
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe == null) {
            throw new RecipeNotFoundException();
        }
        return recipe;
    }

    public Page<Recipe> getRecipesByNameAndCategoryAndNotContainingAllergensPageable(Long categoryId, List<Allergen> allergens, String recipeName,  Pageable pageable) {
        Set<Allergen> allergensSet = new HashSet<>(allergens);
        return recipeRepository.findByCategoriesIdAndAllergensNotInOrNoAllergensAndNameLike(categoryId, allergensSet, recipeName, pageable);
    }

    public Page<Recipe> getRecipesByNamePageable(String recipeName, Pageable pageable) {
        return recipeRepository.findByNameIgnoreCaseContaining(recipeName, pageable);
    }

    public Page<Recipe> getRecipesByNameAndCategoryPageable(Long categoryId, String recipeName, Pageable pageable) {
        return recipeRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, recipeName, pageable);
    }

    public Page<Recipe> getRecipesByNameAndAllergensPageable(String recipeName, List<Allergen> allergens, Pageable pageable) {
        Set<Allergen> allergensSet = new HashSet<>(allergens);
        return recipeRepository.findByNameContainingIgnoreCaseAndAllergensNotInOrNoAllergens(recipeName, allergensSet, pageable);
    }

    public Page<Recipe> getRecipesSortedByIngredientsInFridge(Household household, Pageable pageable) {
        return recipeRepository.findByCountInItemsDesc(household.getId(), pageable);
    }
}