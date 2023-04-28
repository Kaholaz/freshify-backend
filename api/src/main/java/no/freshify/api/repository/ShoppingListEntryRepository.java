package no.freshify.api.repository;

import no.freshify.api.model.ShoppingListEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ShoppingListEntryRepository extends JpaRepository<ShoppingListEntry, Long> {
    List<ShoppingListEntry> findByHouseholdId(long householdId);
}
