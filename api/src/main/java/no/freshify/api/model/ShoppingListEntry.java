package no.freshify.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ShoppingListEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long count;

    @Column(nullable = false)
    private Boolean suggested = false;

    @Column(nullable = false)
    private Boolean checked = false;

    @ManyToOne
    private ItemType type;

    @ManyToOne
    private Household household;

    @ManyToOne
    private User addedBy;
}
