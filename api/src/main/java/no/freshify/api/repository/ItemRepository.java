package no.freshify.api.repository;

import no.freshify.api.model.Household;
import no.freshify.api.model.Item;
import no.freshify.api.model.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByHousehold(Household household);
    List<Item> findItemsByHouseholdAndStatus(Household household, ItemStatus status);
    Item getItemById(Long id);
    Item findByIdAndHousehold(long id, Household household);
}
