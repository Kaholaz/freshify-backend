package no.freshify.api.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HouseholdRecipeDTO {
    private Long id;
    private RecipeDTO recipe;
    private HouseholdDTO household;
}
