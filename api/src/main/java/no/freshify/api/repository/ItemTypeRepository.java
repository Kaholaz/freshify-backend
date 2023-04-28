package no.freshify.api.repository;

import no.freshify.api.model.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemTypeRepository extends JpaRepository<ItemType, Long> {
    @Query(value = "SELECT id, name FROM (SELECT *, LENGTH(name) AS l FROM item_type WHERE LOWER(name) like %?1% " +
            "ORDER BY l limit 10) AS a;", nativeQuery = true)
    List<ItemType> searchItemTypeByName(String name);
}
