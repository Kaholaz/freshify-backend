package no.freshify.api.repository;

import no.freshify.api.model.Household;
import no.freshify.api.model.Item;
import no.freshify.api.model.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByHousehold(Household household);
    List<Item> findItemsByHouseholdAndStatus(Long householdId, ItemStatus status);
}
