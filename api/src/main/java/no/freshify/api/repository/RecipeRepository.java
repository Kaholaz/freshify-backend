package no.freshify.api.repository;

import no.freshify.api.model.recipe.Allergen;
import no.freshify.api.model.recipe.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Page<Recipe> findByCategoriesId(Long categoryId , Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE NOT EXISTS (SELECT a.id FROM r.allergens a WHERE a IN :allergens)")
    Page<Recipe> findAllNotContainingAllergens(List<Allergen> allergens, Pageable pageable);

    @Query("SELECT r FROM Recipe r JOIN r.categories c WHERE c.id = :categories_id AND (r.allergens IS EMPTY OR NOT EXISTS (SELECT a FROM r.allergens a WHERE a IN (:allergens)))")
    Page<Recipe> findByCategoriesIdAndAllergensNotInOrNoAllergens(Long categories_id, Set<Allergen> allergens, Pageable pageable);

    @Query("SELECT r FROM Recipe r JOIN r.categories c WHERE c.id = :categories_id AND (r.allergens IS EMPTY OR NOT EXISTS (SELECT a FROM r.allergens a WHERE a IN (:allergens))) AND LOWER(r.name) LIKE LOWER(CONCAT('%', :recipe_name, '%'))")
    Page<Recipe> findByCategoriesIdAndAllergensNotInOrNoAllergensAndNameLike(Long categories_id, Set<Allergen> allergens, String recipe_name, Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Recipe> findByNameIgnoreCaseContaining(String name, Pageable pageable);

    @Query("SELECT r FROM Recipe r JOIN r.categories c WHERE c.id = :categoryId AND LOWER(r.name) LIKE LOWER(CONCAT('%', :recipeName, '%'))")
    Page<Recipe> findByCategoryIdAndNameContainingIgnoreCase(Long categoryId, String recipeName, Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :recipeName, '%')) AND (r.allergens IS EMPTY OR NOT EXISTS (SELECT a FROM r.allergens a WHERE a IN (:allergens)))")
    Page<Recipe> findByNameContainingIgnoreCaseAndAllergensNotInOrNoAllergens(String recipeName, Set<Allergen> allergens, Pageable pageable);

    @Query("SELECT r FROM Recipe r LEFT JOIN r.recipeIngredients ri LEFT JOIN ItemType it ON ri.itemType.id = it.id LEFT JOIN Item i ON i.type.id = it.id AND i.household.id = :householdId GROUP BY r.id ORDER BY SUM(CASE WHEN i IS NULL THEN 0 ELSE 1 END) DESC")
    Page<Recipe> findByCountInItemsDesc(Long householdId, Pageable pageable);
}
