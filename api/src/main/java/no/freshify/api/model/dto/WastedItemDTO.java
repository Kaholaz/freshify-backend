package no.freshify.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WastedItemDTO {
    private ItemTypeDTO itemType;
    private Number amountWasted;
}
