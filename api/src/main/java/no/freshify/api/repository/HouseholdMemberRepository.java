package no.freshify.api.repository;

import no.freshify.api.model.Household;
import no.freshify.api.model.HouseholdMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HouseholdMemberRepository extends JpaRepository<HouseholdMember, Long> {
    public List<HouseholdMember> findHouseholdMembersByUser(Long userId);
    public List<HouseholdMember> findHouseholdMembersByHousehold(Long householdId);
}
