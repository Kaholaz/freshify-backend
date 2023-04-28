package no.freshify.api.model.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import no.freshify.api.model.Household;
import no.freshify.api.model.dto.HouseholdDTO;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-04-28T10:55:48+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
)
public class HouseholdMapperImpl extends HouseholdMapper {

    @Override
    public HouseholdDTO toHouseholdDTO(Household household) {
        if ( household == null ) {
            return null;
        }

        HouseholdDTO householdDTO = new HouseholdDTO();

        householdDTO.setId( household.getId() );
        householdDTO.setName( household.getName() );

        return householdDTO;
    }

    @Override
    public List<HouseholdDTO> toHouseholdDTO(List<Household> household) {
        if ( household == null ) {
            return null;
        }

        List<HouseholdDTO> list = new ArrayList<HouseholdDTO>( household.size() );
        for ( Household household1 : household ) {
            list.add( toHouseholdDTO( household1 ) );
        }

        return list;
    }
}
