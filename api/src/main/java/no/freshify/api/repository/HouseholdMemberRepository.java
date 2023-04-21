package no.freshify.api.repository;

import no.freshify.api.model.Household;
import no.freshify.api.model.HouseholdMember;
import no.freshify.api.model.HouseholdMemberKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HouseholdMemberRepository extends JpaRepository<HouseholdMember, Long> {
    public List<HouseholdMember> findHouseholdMembersByUserId(Long userId);
    public List<HouseholdMember> findHouseholdMembersByHouseholdId(Long householdId);
    public HouseholdMember save(HouseholdMember householdMember);
    boolean existsById(HouseholdMemberKey householdMemberKey);
    HouseholdMember findByHouseHoldMemberKey(HouseholdMemberKey householdMemberKey);

    Household findHouseholdById(long householdId);
}
