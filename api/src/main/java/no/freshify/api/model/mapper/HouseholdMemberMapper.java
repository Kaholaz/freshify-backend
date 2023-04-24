package no.freshify.api.model.mapper;

import no.freshify.api.model.HouseholdMember;

import no.freshify.api.model.dto.HouseholdMemberDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class HouseholdMemberMapper {
    public abstract HouseholdMemberDTO toHouseholdMemberDTO(HouseholdMember householdMember);
}
