package no.freshify.api.model.dto;

import lombok.Getter;
import lombok.Setter;
import no.freshify.api.model.ItemStatus;

@Getter
@Setter
public class UpdateInventoryItem {
    long itemId;
    double remaining;
    ItemStatus state;
}
