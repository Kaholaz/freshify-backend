package no.freshify.api.model.dto;

import lombok.Getter;
import lombok.Setter;
import no.freshify.api.model.*;

import java.util.Date;

@Getter
@Setter
public class InventoryItem {
    private Long id;
    private Date lastChanged;
    private Double remaining = 1.0;
    private Boolean suggested = false;
    private ItemStatus status;
    private ItemType type;
    private UserFull addedBy;

    public InventoryItem(Item item) {
        this.setId(item.getId());
        this.setLastChanged(item.getLastChanged());
        this.setRemaining(item.getRemaining());
        this.setSuggested(item.getSuggested());
        this.setStatus(item.getStatus());
        this.setType(item.getType());
        this.setAddedBy(new UserFull(item.getAddedBy()));
    }
}
