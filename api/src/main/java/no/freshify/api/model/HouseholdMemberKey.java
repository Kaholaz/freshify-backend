package no.freshify.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class HouseholdMemberKey implements Serializable {
    @Column(name = "household_id")
    private Long householdId;

    @Column(name = "user_id")
    private Long userId;
}
