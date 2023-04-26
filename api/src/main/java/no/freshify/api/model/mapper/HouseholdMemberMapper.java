package no.freshify.api.model.mapper;

import no.freshify.api.model.HouseholdMember;

import no.freshify.api.model.dto.HouseholdMemberDTO;
import no.freshify.api.model.dto.UserFull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper
public abstract class HouseholdMemberMapper {
    @Mappings({@Mapping(target = "userType", source = "role")})
    public abstract HouseholdMemberDTO toHouseholdMemberDTO(HouseholdMember householdMember);
    public abstract List<HouseholdMemberDTO> householdMemberDTOS(List<HouseholdMember> householdMembers);
}
