package no.freshify.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity(name = "_user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<HouseholdMember> householdMembers;

    @OneToMany(mappedBy = "addedBy")
    private Set<Item> items;

    @PreRemove
    private void setItemAddedByToNull() {
        for (Item item : items) {
            item.setAddedBy(null);
        }
    }
}
