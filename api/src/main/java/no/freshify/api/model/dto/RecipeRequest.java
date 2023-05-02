package no.freshify.api.model.dto;

import lombok.Getter;
import lombok.Setter;
import no.freshify.api.model.recipe.Allergen;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RecipeRequest {
    private String name;
    private String description;
    private String steps;
    private Integer estimatedTime;
    private List<RecipeIngredientRequest> recipeIngredients;
    private List<RecipeCategoryRequest> categories;
    private Set<Allergen> allergens;
    private String image;
}