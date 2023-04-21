package no.freshify.api.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShoppingListEntryRequest {
    Long itemTypeId;
    Long count;
    Boolean suggested;
}
