package no.freshify.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    /**
     * Updates the lastChanged field automatically both when created or updated.
     */
    @PrePersist
    @PreUpdate
    public void updateLastChanged() {
        this.lastChanged = new Date();
    }
}
