package no.freshify.api.repository;

import no.freshify.api.model.recipe.HouseholdRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseholdRecipeRepository extends JpaRepository<HouseholdRecipe, Long> {
}