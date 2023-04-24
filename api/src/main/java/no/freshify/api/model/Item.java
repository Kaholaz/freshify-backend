package no.freshify.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import no.freshify.api.exception.IllegalItemParameterException;

import java.util.Date;

@Entity
@Getter
@Setter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date lastChanged;

    @Column(nullable = false)
    private Date addedDate;

    @Column(nullable = false)
    private Double remaining = 1.0;

    @Column(nullable = false)
    private Boolean suggested = false;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @ManyToOne
    private ItemType type;

    @ManyToOne
    private Household household;

    @ManyToOne
    private User addedBy;

    public void setRemaining(Double remaining) throws IllegalItemParameterException {
        if (remaining < 0 || remaining > 1) {
            throw new IllegalItemParameterException("Remaining must be between 0 and 1");
        }
        this.remaining = remaining;
    }

    /**
     * Sets addedDate and lastChanged date automatically when created.
     */
    @PrePersist
    public void setAddedDate() {
        this.addedDate = new Date();
        this.lastChanged = this.addedDate;
    }

    /**
     * Updates the lastChanged field automatically when updated.
     */
    @PreUpdate
    public void updateLastChanged() {
        this.lastChanged = new Date();
    }
}
