package no.freshify.api.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShoppingListEntryEditRequest {
    Long id;
    Long count;
    Boolean suggested;
    Boolean checked;
}
