package no.freshify.api.repository;

import no.freshify.api.model.recipe.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>, CustomizedRecipeRepository {
    Page<Recipe> findByCategoriesId(Long categoryId , Pageable pageable);
}
