package no.freshify.api.repository;

import no.freshify.api.model.recipe.Allergen;
import no.freshify.api.model.recipe.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Page<Recipe> findByCategoriesId(Long categoryId , Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE NOT EXISTS (SELECT a.id FROM r.allergens a WHERE a IN :allergens)")
    Page<Recipe> findAllNotContainingAllergens(List<Allergen> allergens, Pageable pageable);

    @Query("SELECT r FROM Recipe r JOIN r.categories c WHERE c.id = :categories_id AND (r.allergens IS EMPTY OR NOT EXISTS (SELECT a FROM r.allergens a WHERE a IN (:allergens)))")
    Page<Recipe> findByCategoriesIdAndAllergensNotInOrNoAllergens(Long categories_id, Set<Allergen> allergens, Pageable pageable);
}
