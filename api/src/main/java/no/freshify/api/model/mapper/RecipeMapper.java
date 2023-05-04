package no.freshify.api.model.mapper;

import no.freshify.api.model.dto.*;
import no.freshify.api.model.recipe.Allergen;
import no.freshify.api.model.recipe.Recipe;
import no.freshify.api.model.recipe.RecipeCategory;
import no.freshify.api.model.recipe.RecipeIngredient;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(uses = {UserMapper.class})
public abstract class RecipeMapper {
    public abstract RecipeRequest toRecipeRequest(Recipe recipe);
    public abstract List<RecipeRequest> toRecipeRequest(List<Recipe> recipes);

    public abstract Recipe toRecipe(RecipeRequest recipeRequest);

    public abstract RecipeIngredientRequest toRecipeIngredientRequest(RecipeIngredient recipeIngredient);
    public abstract RecipeIngredient toRecipeIngredient(RecipeIngredientRequest recipeIngredientRequest);

    public abstract List<RecipeIngredientRequest> toRecipeIngredientRequest(List<RecipeIngredient> recipeIngredients);
    public abstract List<RecipeIngredient> toRecipeIngredient(List<RecipeIngredientRequest> recipeIngredientRequests);

    public abstract RecipeCategoryRequest toRecipeCategoryRequest(RecipeCategory recipeCategory);
    public abstract List<RecipeCategoryRequest> toRecipeCategoryRequest(List<RecipeCategory> recipeCategories);

    public abstract RecipeDTO toRecipeDTO(Recipe recipe);
    public abstract List<RecipeDTO> toRecipeDTO(List<Recipe> recipes);

    public abstract RecipeCategoryDTO recipeCategoryDTO(RecipeCategory recipeCategory);
    public abstract List<RecipeCategoryDTO> recipeCategoryDTO(List<RecipeCategory> recipeCategories);

    public abstract RecipeIngredientDTO recipeIngredientDTO(RecipeIngredient recipeIngredient);
    public abstract List<RecipeIngredientDTO> recipeIngredientDTO(List<RecipeIngredient> recipeIngredients);

    public abstract AllergenDTO allergenDTO(Allergen allergen);
    public abstract List<AllergenDTO> allergenDTO(List<Allergen> allergens);

    public Page<RecipeDTO> toRecipeDTOPage(Page<Recipe> recipePage) {
        List<RecipeDTO> recipeDTOList = toRecipeDTO(recipePage.getContent());
        return new PageImpl<>(recipeDTOList, recipePage.getPageable(), recipePage.getTotalElements());
    }
}