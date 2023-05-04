package no.freshify.api.service;

import no.freshify.api.exception.*;
import no.freshify.api.model.Household;
import no.freshify.api.model.Item;
import no.freshify.api.model.ItemType;
import no.freshify.api.model.recipe.*;
import no.freshify.api.repository.HouseholdRecipeRepository;
import no.freshify.api.repository.HouseholdRepository;
import no.freshify.api.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class HouseholdRecipeServiceTest {

    @Mock
    private HouseholdRecipeRepository householdRecipeRepository;

    @Mock
    private HouseholdRepository householdRepository;

    @InjectMocks
    private HouseholdRecipeService householdRecipeService;

    private HouseholdRecipe householdRecipe;
    private List<HouseholdRecipe> householdRecipes;
    private Recipe recipe;
    private Set<Recipe> recipes;
    private List<Item> items;
    private ItemType itemType;
    private HashSet<ItemType> itemTypes;
    private Item item;
    private RecipeIngredient recipeIngredient;
    private Set<RecipeIngredient> recipeIngredients;
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

        itemTypes = new HashSet<>();
        itemTypes.add(itemType);

        item = new Item();
        item.setId(1L);
        item.setType(itemType);
        items = new ArrayList<>();
        items.add(item);

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

        householdRecipe = new HouseholdRecipe();
        householdRecipe.setId(1L);
        householdRecipe.setRecipe(recipe);
        householdRecipe.setHousehold(household);

        householdRecipes = new ArrayList<>();
        householdRecipes.add(householdRecipe);

        household = new Household();
        household.setId(1L);

        recipes = new HashSet<>();
        recipes.add(recipe);

        page = new PageImpl<>(recipes.stream().toList());
    }

    @Test
    public void testAddRecipe_Success() {
        when(householdRecipeRepository.save(any(HouseholdRecipe.class))).thenReturn(householdRecipe);

        HouseholdRecipe result = householdRecipeService.addRecipe(householdRecipe);

        assertEquals(householdRecipe, result);
    }

    @Test
    public void testGetRecipe_Success() {
        when(householdRecipeRepository.findByHouseholdIdAndRecipeId(anyLong(), anyLong()))
                .thenReturn(householdRecipe);

        HouseholdRecipe result = householdRecipeService.getRecipe(household.getId(), householdRecipe.getId());

        assertEquals(householdRecipe, result);
    }

    @Test
    public void testGetRecipes_Success() throws HouseholdNotFoundException {
        when(householdRepository.existsById(anyLong())).thenReturn(true);
        when(householdRecipeRepository.findAllByHouseholdId(anyLong())).thenReturn(householdRecipes);

        List<HouseholdRecipe> result = householdRecipeService.getRecipes(household.getId());

        assertEquals(householdRecipes, result);
    }

    @Test
    public void testGetRecipes_ThrowsHouseholdNotFoundException() {
        when(householdRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(HouseholdNotFoundException.class, () -> householdRecipeService.getRecipes(123L));
    }

    @Test
    public void testRemoveHouseholdRecipe_Success() throws HouseholdRecipeNotFoundException {
        when(householdRecipeRepository.findByHouseholdIdAndRecipeId(anyLong(), anyLong())).thenReturn(householdRecipe);
        doNothing().when(householdRecipeRepository).delete(any(HouseholdRecipe.class));

        householdRecipeService.removeHouseholdRecipe(household.getId(), householdRecipe.getId());

        verify(householdRecipeRepository, times(1)).delete(any(HouseholdRecipe.class));
    }
}
