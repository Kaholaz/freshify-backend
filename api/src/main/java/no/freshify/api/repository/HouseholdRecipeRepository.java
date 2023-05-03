package no.freshify.api.repository;

import no.freshify.api.model.recipe.HouseholdRecipe;
import no.freshify.api.model.recipe.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseholdRecipeRepository extends JpaRepository<HouseholdRecipe, Long> {
    List<HouseholdRecipe> findAllByHouseholdId(Long householdId);

    HouseholdRecipe findByHouseholdIdAndRecipeId(Long householdId, Long id);
}