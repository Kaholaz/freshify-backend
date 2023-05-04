package no.freshify.api.model.mapper;

import no.freshify.api.model.dto.AllergenDTO;
import no.freshify.api.model.recipe.Allergen;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public abstract class AllergenMapper {
    public abstract AllergenDTO toAllergenDTO(Allergen allergen);
    public abstract List<AllergenDTO> toAllergenDTO(List<Allergen> allergens);
}
