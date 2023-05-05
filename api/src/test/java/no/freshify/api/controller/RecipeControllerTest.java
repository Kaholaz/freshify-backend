package no.freshify.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.freshify.api.exception.IllegalItemParameterException;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.*;
import no.freshify.api.model.mapper.RecipeMapper;
import no.freshify.api.model.recipe.*;
import no.freshify.api.security.UserAuthentication;
import no.freshify.api.security.UserDetailsImpl;
import no.freshify.api.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HouseholdService householdService;

    @MockBean
    private UserService userService;

    @MockBean
    private AllergenService allergenService;

    @MockBean
    private HouseholdRecipeService householdRecipeService;

    @MockBean
    private RecipeCategoryService recipeCategoryService;

    @MockBean
    private ItemTypeService itemTypeService;

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private RecipeMapper recipeMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserDetailsImpl userDetails;

    private Household household;
    private User user;
    private ItemType itemType;
    private RecipeRequest recipeRequest;
    private Item item;
    private Map<String, Object> requestBody;
    private List<Map<String, Object>> requestBodyList;
    private HouseholdRecipe householdRecipe;
    private Recipe recipe;
    private RecipeDTO recipeDTO;
    private Set<Recipe> recipes;
    private RecipeIngredient recipeIngredient;
    private Set<RecipeIngredient> recipeIngredients;
    private Allergen allergen;
    private Set<Allergen> allergens;
    private RecipeCategory recipeCategory;
    private Set<RecipeCategory> categories;
    private Page<Recipe> page;

    private Authentication authentication;

    @BeforeEach
    public void setup() throws IllegalItemParameterException {
        household = new Household();
        household.setId(1L);
        household.setName("Test Household");

        user = new User();
        user.setId(1L);
        user.setEmail("test@");

        itemType = new ItemType();
        itemType.setId(1L);
        itemType.setName("Test Item Type");

        item = new Item();
        item.setId(1L);
        item.setType(itemType);
        item.setStatus(ItemStatus.INVENTORY);
        item.setHousehold(household);
        item.setAddedBy(user);

        userDetails = new UserDetailsImpl(user.getId(), user.getEmail(), "password", user.getPassword(), Collections.emptyList());

        requestBody = new HashMap<>();
        requestBody.put("itemTypeId", 1L);
        requestBody.put("count", 1);

        requestBodyList = new ArrayList<>();
        requestBodyList.add(requestBody);

        authentication = new UserAuthentication(userDetails);

        SecurityContextHolder.getContext().setAuthentication(authentication);

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

        recipeCategory = new RecipeCategory();
        recipeCategory.setId(1L);
        recipeCategory.setRecipes(recipes);
        recipeCategory.setName("Category");

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

        recipeDTO = recipeMapper.toRecipeDTO(recipe);

        household = new Household();
        household.setId(1L);

        recipes = new HashSet<>();
        recipes.add(recipe);

        page = new PageImpl<>(recipes.stream().toList());

        ItemTypeDTO itemTypeDTO = new ItemTypeDTO();
        itemTypeDTO.setId(1L);

        RecipeIngredientRequest recipeIngredientRequest = new RecipeIngredientRequest();
        recipeIngredientRequest.setAmount(1D);
        recipeIngredientRequest.setUnit("Unit");
        recipeIngredientRequest.setItemType(itemTypeDTO);

        List<RecipeIngredientRequest> recipeIngredientRequests = new ArrayList<>();
        recipeIngredientRequests.add(recipeIngredientRequest);

        RecipeCategoryRequest recipeCategoryRequest = new RecipeCategoryRequest();
        recipeCategoryRequest.setId(1L);
        recipeCategoryRequest.setName("Category");

        List<RecipeCategoryRequest> recipeCategoryRequests = new ArrayList<>();
        recipeCategoryRequests.add(recipeCategoryRequest);

        recipeRequest = new RecipeRequest();
        recipeRequest.setRecipeIngredients(recipeIngredientRequests);
        recipeRequest.setImage("Image");
        recipeRequest.setName("Recipe");
        recipeRequest.setDescription("Description");
        recipeRequest.setAllergens(allergens);
        recipeRequest.setCategories(recipeCategoryRequests);
        recipeRequest.setSteps("Steps");
        recipeRequest.setEstimatedTime(1);

        householdRecipe = new HouseholdRecipe();
        householdRecipe.setId(1L);
        householdRecipe.setHousehold(household);
        householdRecipe.setRecipe(recipe);
    }

    @Test
    public void testCreateRecipe_Success() throws Exception {
        when(recipeMapper.toRecipe(any(RecipeRequest.class))).thenReturn(recipe);
        when(allergenService.getAllergenById(anyLong())).thenReturn(allergen);
        when(recipeCategoryService.getRecipeCategoryById(anyLong())).thenReturn(recipeCategory);
        when(itemTypeService.getItemTypeById(anyLong())).thenReturn(itemType);
        when(recipeMapper.toRecipeDTO(any(Recipe.class))).thenReturn(recipeDTO);
        when(recipeService.addRecipe(any(Recipe.class))).thenReturn(recipe);

        System.out.println(itemType.getId());
        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(recipe.getId().intValue())))
                .andExpect(jsonPath("$.name", is(recipe.getName())))
                .andExpect(jsonPath("$.description", is(recipe.getDescription())))
                .andExpect(jsonPath("$.image", is(recipe.getImage())))
                .andExpect(jsonPath("$.steps", is(recipe.getSteps())))
                .andExpect(jsonPath("$.estimatedTime", is(recipe.getEstimatedTime())))
                .andReturn();

        verify(allergenService, VerificationModeFactory.times(1)).getAllergenById(anyLong());
        verify(recipeCategoryService, VerificationModeFactory.times(1)).getRecipeCategoryById(anyLong());
        verify(itemTypeService, VerificationModeFactory.times(1)).getItemTypeById(anyLong());
        verify(recipeService, VerificationModeFactory.times(1)).addRecipe(any(Recipe.class));
    }

    @Test
    public void testGetRecipeById() throws Exception {
        when(recipeService.getRecipeById(anyLong())).thenReturn(recipe);
        when(householdService.findHouseholdByHouseholdId(anyLong())).thenReturn(household);
        when(recipeMapper.toRecipeDTO(any(Recipe.class))).thenReturn(recipeDTO);

        mockMvc.perform(get("/recipes/{householdId}/recipe/{id}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(recipe.getId().intValue())))
                .andExpect(jsonPath("$.name", is(recipe.getName())))
                .andExpect(jsonPath("$.description", is(recipe.getDescription())))
                .andExpect(jsonPath("$.steps", is(recipe.getSteps())))
                .andExpect(jsonPath("$.estimatedTime", is(recipe.getEstimatedTime())))
                .andReturn();

        verify(recipeService, VerificationModeFactory.times(1)).getRecipeById(anyLong());
        verify(householdService, VerificationModeFactory.times(1)).findHouseholdByHouseholdId(anyLong());
    }
}
