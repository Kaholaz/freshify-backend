package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.AllergenNotFoundException;
import no.freshify.api.model.recipe.Allergen;
import no.freshify.api.repository.AllergenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllergenService {
    private final AllergenRepository allergenRepository;

    public Allergen getAllergenById(Long id) throws AllergenNotFoundException {
        return allergenRepository.findById(id).orElseThrow(AllergenNotFoundException::new);
    }

    public List<Allergen> getAllergensByIds(List<Long> allergensIds) {
        return allergenRepository.findAllById(allergensIds);
    }

    public List<Allergen> getAllergens() {
        return allergenRepository.findAll();
    }
}
