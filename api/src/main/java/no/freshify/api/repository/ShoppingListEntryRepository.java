package no.freshify.api.repository;

import no.freshify.api.model.ShoppingListEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingListEntryRepository extends JpaRepository<ShoppingListEntry, Long> {
}
