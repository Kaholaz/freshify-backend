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
    private String name;
    private Integer countWasted;
    private Double amountWasted;
}
