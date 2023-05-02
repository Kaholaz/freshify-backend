package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.RecipeCategoryNotFoundException;
import no.freshify.api.model.recipe.RecipeCategory;
import no.freshify.api.repository.RecipeCategoryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeCategoryService {
    private final RecipeCategoryRepository recipeCategoryRepository;

    public RecipeCategory getRecipeCategoryById(Long id) throws RecipeCategoryNotFoundException {
        return recipeCategoryRepository.findById(id).orElseThrow(RecipeCategoryNotFoundException::new);
    }
}
