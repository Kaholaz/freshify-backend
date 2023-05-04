package no.freshify.api.repository;

import no.freshify.api.model.RecipeFilter;
import no.freshify.api.model.recipe.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CustomizedRecipeRepository {
    Page<Recipe> findAll(RecipeFilter filter, PageRequest pageRequest);
}
