package no.freshify.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Filter for recipes
 */
@Getter
@Setter
public class RecipeFilter {
    private String searchString = null;
    private List<Long> categoryIds = null;
    private List<Long> allergenIds = null;
    private Long householdId = null;
    private Boolean sortByIngredientsInFridge = false;
    private Boolean getHouseholdRecipes;

    public static class RecipeFilterBuilder {
        private final RecipeFilter filter = new RecipeFilter();

        private RecipeFilterBuilder() {
        }

        public RecipeFilterBuilder searchString(String searchString) {
            filter.setSearchString(searchString);
            return this;
        }

        public RecipeFilterBuilder categoryIds(List<Long> categories) {
            filter.setCategoryIds(categories);
            return this;
        }

        public RecipeFilterBuilder allergenIds(List<Long> categories) {
            filter.setAllergenIds(categories);
            return this;
        }

        public RecipeFilterBuilder householdId(Long householdId) {
            filter.setHouseholdId(householdId);
            return this;
        }

        public RecipeFilterBuilder sortByIngredientsInFridge(boolean sortByIngredientsInFridge) {
            filter.setSortByIngredientsInFridge(sortByIngredientsInFridge);
            return this;
        }

        public RecipeFilterBuilder getHouseholdRecipes(Boolean getHouseholdRecipes) {
            filter.setGetHouseholdRecipes(getHouseholdRecipes);
            return this;
        }

        public RecipeFilter build() {
            return filter;
        }
    }

    public static RecipeFilterBuilder builder() {
        return new RecipeFilterBuilder();
    }
}