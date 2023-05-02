package no.freshify.api.model.dto;

import lombok.Getter;
import lombok.Setter;
import no.freshify.api.model.ItemType;

@Getter
@Setter
public class RecipeIngredientDTO {
    private long id;
    private ItemType itemType;
    private double amount;
    private String unit;
}
