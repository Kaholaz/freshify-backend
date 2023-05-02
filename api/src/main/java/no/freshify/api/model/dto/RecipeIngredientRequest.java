package no.freshify.api.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipeIngredientRequest {
    private ItemTypeDTO itemType;
    private double amount;
    private String unit;
}