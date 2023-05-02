package no.freshify.api.model.recipe;

import jakarta.persistence.Entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import no.freshify.api.model.ItemType;

@Entity
@Getter
@Setter
public class RecipeIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private ItemType itemType;

    @Column
    private double amount;

    @Column
    private String unit;

    @ManyToOne
    private Recipe recipe;
}
