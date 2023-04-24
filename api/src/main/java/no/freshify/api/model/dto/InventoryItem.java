package no.freshify.api.model.dto;

import lombok.Getter;
import lombok.Setter;
import no.freshify.api.model.ItemStatus;
import no.freshify.api.model.ItemType;

import java.util.Date;

@Getter
@Setter
public class InventoryItem {
    private Long id;
    private Date lastChanged;
    private Date addedDate;
    private Double remaining = 1.0;
    private Boolean suggested = false;
    private ItemStatus status;
    private ItemType type;
    private UserFull addedBy;
}
