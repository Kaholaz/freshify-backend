package no.freshify.api.model.dto;

import lombok.Getter;
import lombok.Setter;
import no.freshify.api.model.HouseholdMemberKey;
import no.freshify.api.model.HouseholdMemberRole;

@Getter
@Setter
public class HouseholdMemberDTO {
    private HouseholdMemberKey id;
    private UserFull user;
    private HouseholdMemberRole role;
}
