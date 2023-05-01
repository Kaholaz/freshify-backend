package no.freshify.api.repository;

import no.freshify.api.model.Household;
import no.freshify.api.model.Item;
import no.freshify.api.model.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByHousehold(Household household);
    List<Item> findItemsByHouseholdAndStatus(Household household, ItemStatus status);
    Item getItemById(Long id);
    Item findByIdAndHousehold(long id, Household household);
    List<Item> findItemsByHouseholdAndStatusAndLastChangedBetweenAndRemainingGreaterThan
            (Household household, ItemStatus status, Date startDate, Date endDate, Double remaining);
}
