package no.freshify.api.model.dto;

import lombok.Getter;
import lombok.Setter;
import no.freshify.api.model.recipe.Allergen;
import no.freshify.api.model.recipe.RecipeCategory;
import no.freshify.api.model.recipe.RecipeIngredient;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RecipeDTO {
    private Long id;
    private Integer estimatedTime;
    private String name;
    private String description;
    private String steps;
    private Set<RecipeCategoryDTO> categories;
    private Boolean isInHousehold = false;
    private Integer totalIngredientsInFridge = 0;
    private Set<RecipeIngredientDTO> recipeIngredients;
    private Set<AllergenDTO> allergens;
    private String image;
}
