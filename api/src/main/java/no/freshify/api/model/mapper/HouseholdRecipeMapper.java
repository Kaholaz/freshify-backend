package no.freshify.api.model.mapper;

import no.freshify.api.model.dto.HouseholdRecipeDTO;
import no.freshify.api.model.recipe.HouseholdRecipe;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public abstract class HouseholdRecipeMapper {

    public abstract HouseholdRecipeDTO toHouseholdRecipeDTO(HouseholdRecipe householdRecipe);
    public abstract List<HouseholdRecipeDTO> toHouseholdRecipeDTOs(List<HouseholdRecipe> recipes);
}
