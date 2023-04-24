package no.freshify.api.model.mapper;

import no.freshify.api.model.Household;
import no.freshify.api.model.dto.HouseholdDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public abstract class HouseholdMapper {
    public abstract HouseholdDTO toHouseholdDTO(Household household);
    public abstract List<HouseholdDTO> toHouseholdDTO(List<Household> household);
}
