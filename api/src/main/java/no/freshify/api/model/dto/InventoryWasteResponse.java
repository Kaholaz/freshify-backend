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
public class InventoryWasteResponse {
    List<WastedItemDTO> wastedItems;
    Integer total;
    Double average;
}
