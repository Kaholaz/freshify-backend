package no.freshify.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Household {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "household")
    private Set<HouseholdMember> householdMembers;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "household")
    private Set<Item> items;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "household")
    private Set<ShoppingListEntry> shoppingLists;
}
