package no.freshify.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.freshify.api.model.ItemType;
import no.freshify.api.model.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingListEntryResponse {
    Long id;
    Long count;
    Boolean suggested;
    Boolean checked;
    ItemType type;
}
