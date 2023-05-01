package no.freshify.api.model.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import no.freshify.api.model.HouseholdMember;
import no.freshify.api.model.User;
import no.freshify.api.model.dto.HouseholdMemberDTO;
import no.freshify.api.model.dto.UserFull;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-01T11:30:05+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
)
public class HouseholdMemberMapperImpl extends HouseholdMemberMapper {

    @Override
    public HouseholdMemberDTO toHouseholdMemberDTO(HouseholdMember householdMember) {
        if ( householdMember == null ) {
            return null;
        }

        HouseholdMemberDTO householdMemberDTO = new HouseholdMemberDTO();

        householdMemberDTO.setUserType( householdMember.getRole() );
        householdMemberDTO.setId( householdMember.getId() );
        householdMemberDTO.setUser( userToUserFull( householdMember.getUser() ) );

        return householdMemberDTO;
    }

    @Override
    public List<HouseholdMemberDTO> householdMemberDTOS(List<HouseholdMember> householdMembers) {
        if ( householdMembers == null ) {
            return null;
        }

        List<HouseholdMemberDTO> list = new ArrayList<HouseholdMemberDTO>( householdMembers.size() );
        for ( HouseholdMember householdMember : householdMembers ) {
            list.add( toHouseholdMemberDTO( householdMember ) );
        }

        return list;
    }

    protected UserFull userToUserFull(User user) {
        if ( user == null ) {
            return null;
        }

        UserFull userFull = new UserFull();

        userFull.setId( user.getId() );
        userFull.setFirstName( user.getFirstName() );
        userFull.setEmail( user.getEmail() );

        return userFull;
    }
}
