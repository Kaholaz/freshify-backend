package no.freshify.api.service;

import no.freshify.api.exception.ItemTypeNotFoundException;
import no.freshify.api.exception.RecipeNotFoundException;
import no.freshify.api.exception.ShoppingListEntryAlreadyExistsException;
import no.freshify.api.model.Household;
import no.freshify.api.model.ItemType;
import no.freshify.api.model.recipe.Allergen;
import no.freshify.api.model.recipe.Recipe;
import no.freshify.api.model.recipe.RecipeCategory;
import no.freshify.api.model.recipe.RecipeIngredient;
import no.freshify.api.repository.RecipeIngredientRepository;
import no.freshify.api.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeIngredientService recipeIngredientService;

    private Recipe recipe;
    private Set<Recipe> recipes;
    private RecipeIngredient recipeIngredient;
    private Set<RecipeIngredient> recipeIngredients;
    private ItemType itemType;
    private Allergen allergen;
    private Set<Allergen> allergens;
    private RecipeCategory category;
    private Household household;
    private Set<RecipeCategory> categories;
    private Page<Recipe> page;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        itemType = new ItemType();
        itemType.setName("ItemType");
        itemType.setId(1L);

        recipeIngredient = new RecipeIngredient();
        recipeIngredient.setId(1L);
        recipeIngredient.setUnit("Unit");
        recipeIngredient.setAmount(1D);
        recipeIngredient.setRecipe(recipe);
        recipeIngredient.setItemType(itemType);

        allergen = new Allergen();
        allergen.setId(1L);
        allergen.setName("Allergen");
        allergens = new HashSet<>();
        allergens.add(allergen);

        category = new RecipeCategory();
        category.setId(1L);
        category.setRecipes(recipes);
        category.setName("Category");

        recipeIngredients = new HashSet<>();
        recipeIngredients.add(recipeIngredient);

        recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Recipe");
        recipe.setImage("Image");
        recipe.setDescription("Description");
        recipe.setSteps("Steps");
        recipe.setEstimatedTime(1);
        recipe.setRecipeIngredients(recipeIngredients);
        recipe.setAllergens(allergens);
        recipe.setCategories(categories);

        household = new Household();
        household.setId(1L);

        recipes = new HashSet<>();
        recipes.add(recipe);

        page = new PageImpl<>(recipes.stream().toList());
    }

    @Test
    public void testAddRecipe() {
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);
        doNothing().when(recipeIngredientService).addRecipeIngredients(anySet());

        Recipe result = recipeService.addRecipe(recipe);

        assertEquals(recipe, result);
    }

    @Test
    public void testGetRecipesByCategoryPageable_Success() {
        when(recipeRepository.findByCategoriesId(anyLong(), any(Pageable.class))).thenReturn(page);

        Page<Recipe> result = recipeService.getRecipesByCategoryPageable(category.getId(), Pageable.unpaged());

        assertEquals(recipe, result.get().toList().get(0));
    }

    @Test
    public void testGetRecipesPageable_Success() {
        when(recipeRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Recipe> result = recipeService.getRecipesPageable(Pageable.unpaged());

        assertEquals(recipe, result.get().toList().get(0));
    }

    @Test
    public void testGetRecipesNotContainingAllergensPageable_Success() {
        when(recipeRepository.findAllNotContainingAllergens(anyList(), any(Pageable.class)))
                .thenReturn(page);

        Page<Recipe> result = recipeService.getRecipesNotContainingAllergensPageable(allergens.stream().toList(), Pageable.unpaged());

        assertEquals(recipe, result.get().toList().get(0));
    }

    @Test
    public void testGetRecipeById_Success() throws RecipeNotFoundException {
        when(recipeRepository.findById(any(Long.class))).thenReturn(ofNullable(recipe));

        Recipe result = recipeService.getRecipeById(recipe.getId());

        assertEquals(recipe, result);
    }

    @Test
    public void testGetRecipeById_ThrowsRecipeNotFoundException() {
        when(recipeRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> {
            recipeService.getRecipeById(recipe.getId());
        });
    }

    @Test
    public void getRecipesByNameAndCategoryAndNotContainingAllergensPageable_Success() {
        when(recipeRepository.findByCategoriesIdAndAllergensNotInOrNoAllergensAndNameLike(anyLong(), anySet(), anyString(), any(Pageable.class)))
                .thenReturn(page);

        Page<Recipe> result = recipeService.getRecipesByNameAndCategoryAndNotContainingAllergensPageable(category.getId(), allergens.stream().toList(), recipe.getName(), Pageable.unpaged());

        assertEquals(recipe, result.get().toList().get(0));
    }

    @Test
    public void testGetRecipesByNamePageable_Success() {
        when(recipeRepository.findByNameIgnoreCaseContaining(anyString(), any(Pageable.class)))
                .thenReturn(page);

        Page<Recipe> result = recipeService.getRecipesByNamePageable("Recipe", Pageable.unpaged());

        assertEquals(recipe, result.get().toList().get(0));
    }

    @Test
    public void testGetRecipesByNameAndCategoryPageable_Success() {
        when(recipeRepository.findByCategoryIdAndNameContainingIgnoreCase(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(page);

        Page<Recipe> result = recipeService.getRecipesByNameAndCategoryPageable(1L, "Recipe", Pageable.unpaged());

        assertEquals(recipe, result.get().toList().get(0));
    }

    @Test
    public void testGetRecipesByNameAndAllAllergensPageable_Success() {
        when(recipeRepository.findByNameContainingIgnoreCaseAndAllergensNotInOrNoAllergens(anyString(), anySet(), any(Pageable.class)))
                .thenReturn(page);

        Page<Recipe> result = recipeService.getRecipesByNameAndAllergensPageable("Recipe", allergens.stream().toList(), Pageable.unpaged());

        assertEquals(recipe, result.get().toList().get(0));
    }

    @Test
    public void getRecipesSortedByIngredientsInFridge_Success() {
        when(recipeRepository.findByCountInItemsDesc(anyLong(), any(Pageable.class))).thenReturn(page);

        Page<Recipe> result = recipeService.getRecipesSortedByIngredientsInFridge(household, Pageable.unpaged());

        assertEquals(recipe, result.get().toList().get(0));
    }
}
