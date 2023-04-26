package no.freshify.api.model.mapper;

import no.freshify.api.model.HouseholdMember;

import no.freshify.api.model.dto.HouseholdMemberDTO;
import no.freshify.api.model.dto.UserFull;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public abstract class HouseholdMemberMapper {
    public abstract HouseholdMemberDTO toHouseholdMemberDTO(HouseholdMember householdMember);
    public abstract List<HouseholdMemberDTO> householdMemberDTOS(List<HouseholdMember> householdMembers);
}
