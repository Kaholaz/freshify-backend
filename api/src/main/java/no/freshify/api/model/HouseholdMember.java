package no.freshify.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class HouseholdMember {
    @EmbeddedId
    private HouseholdMemberKey id;

    @ManyToOne
    @MapsId("householdId")
    @JoinColumn(name = "household_id")
    private Household household;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private HouseholdMemberRole role;
}
