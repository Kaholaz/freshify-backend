package no.freshify.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WasteSortedListsResponse {
    private List<WastedItemDTO> wastedItemsByCount;
    private List<WastedItemDTO> wastedItemsByAverageAmount;
}
