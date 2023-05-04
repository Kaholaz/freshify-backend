package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.RecipeNotFoundException;
import no.freshify.api.model.Household;
import no.freshify.api.model.RecipeFilter;
import no.freshify.api.model.recipe.Allergen;
import no.freshify.api.model.recipe.Recipe;
import no.freshify.api.repository.CustomizedRecipeRepository;
import no.freshify.api.repository.RecipeRepository;
import org.hibernate.service.spi.InjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public Page<Recipe> getRecipesByCategoryPageable(Long categoryId, Pageable pageable) {
        return recipeRepository.findByCategoriesId(categoryId, pageable);
    }

    public Page<Recipe> getRecipesByFilter(RecipeFilter filter, PageRequest pageRequest) {
        return recipeRepository.findAll(filter, pageRequest);
    }

    public Page<Recipe> getRecipesPageable(Pageable pageable) {
        return recipeRepository.findAll(pageable);
    }

    public Recipe addRecipe(Recipe recipe) {
        try {
            return recipeRepository.save(recipe);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Could not add recipe");
        }
    }


    public Recipe getRecipeById(Long id) throws RecipeNotFoundException {
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe == null) {
            throw new RecipeNotFoundException();
        }
        return recipe;
    }
}