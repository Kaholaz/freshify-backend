package no.freshify.api.model.recipe;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import no.freshify.api.model.Household;

@Entity
@Getter
@Setter
public class HouseholdRecipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Recipe recipe;

    @OneToOne
    private Household household;

    @Column
    private int servings;
}
