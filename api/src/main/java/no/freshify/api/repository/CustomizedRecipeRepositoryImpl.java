package no.freshify.api.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;

import no.freshify.api.model.ItemType;
import no.freshify.api.model.RecipeFilter;
import no.freshify.api.model.recipe.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.support.PageableExecutionUtils;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class CustomizedRecipeRepositoryImpl implements CustomizedRecipeRepository {
    private final EntityManager entityManager;
    private final RecipeRepository recipeRepository;
    private final ItemRepository itemRepository;


    private long countAvailableIngredients(Recipe recipe, Long householdId) {
        long count = 0;
        for (RecipeIngredient ingredient : recipe.getRecipeIngredients()) {
            ItemType itemType = ingredient.getItemType();
            long itemCount = itemRepository.countByTypeAndHouseholdId(itemType, householdId);
            if (itemCount > 0) {
                count++;
            }
        }
        return count;
    }

    private <T> Predicate searchLowerCase(CriteriaBuilder cb, Root<T> root, String field, String value) {
        return cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
    }

    public Page<Recipe> findAll(RecipeFilter filter, PageRequest pageRequest) {
        if (filter == null) return recipeRepository.findAll(pageRequest);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Recipe> cq = cb.createQuery(Recipe.class);
        Root<Recipe> recipe = cq.from(Recipe.class);

        Predicate searchString;
        if (filter.getSearchString() != null) {
            Predicate nameSearchString = searchLowerCase(cb, recipe, "name", filter.getSearchString());
            Predicate descriptionSearchString = searchLowerCase(cb, recipe, "description", filter.getSearchString());
            searchString = cb.or(nameSearchString, descriptionSearchString);
        } else {
            searchString = cb.conjunction();
        }

        Predicate categoryFilter;
        if (filter.getCategoryIds() != null) {
            Join<Recipe, RecipeCategory> categoryJoin = recipe.join("categories");
            categoryFilter = categoryJoin.get("id").in(filter.getCategoryIds());
        } else {
            categoryFilter = cb.conjunction();
        }

        Predicate allergenFilter;
        if (filter.getAllergenIds() != null) {
            Join<Recipe, Allergen> allergenJoin = recipe.join("allergens");
            allergenFilter = cb.not(allergenJoin.get("id").in(filter.getAllergenIds()));
        } else {
            allergenFilter = cb.conjunction();
        }

        Predicate householdRecipeFilter;
        if (filter.getGetHouseholdRecipes() != null) {
            Subquery<Long> householdRecipeSubquery = cq.subquery(Long.class);
            Root<HouseholdRecipe> householdRecipeRoot = householdRecipeSubquery.from(HouseholdRecipe.class);
            householdRecipeSubquery.select(householdRecipeRoot.get("recipe").get("id"))
                    .where(cb.equal(householdRecipeRoot.get("household").get("id"), filter.getHouseholdId()));

            if (filter.getGetHouseholdRecipes()) {
                householdRecipeFilter = recipe.get("id").in(householdRecipeSubquery);
            } else {
                householdRecipeFilter = cb.not(recipe.get("id").in(householdRecipeSubquery));
            }
        } else {
            householdRecipeFilter = cb.conjunction();
        }

        Predicate finalFilter = cb.and(searchString, categoryFilter, allergenFilter, householdRecipeFilter);
        cq.select(recipe).where(finalFilter).distinct(true);

        List<Recipe> recipes = entityManager.createQuery(cq.select(recipe).where(finalFilter)).getResultList();

        if (filter.getSortByIngredientsInFridge()) {
            recipes.sort(Comparator.comparingLong((Recipe r) -> this.countAvailableIngredients(r, filter.getHouseholdId())).reversed());
        }

        long countResult = recipes.size();
        recipes = recipes.stream().skip(pageRequest.getOffset()).limit(pageRequest.getPageSize()).toList();
        return PageableExecutionUtils.getPage(recipes, pageRequest, () -> countResult);
    }
}