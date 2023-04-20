package no.freshify.api.repository;

import no.freshify.api.model.Household;
import no.freshify.api.model.HouseholdMember;
import no.freshify.api.model.HouseholdMemberKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseholdMemberRepository extends JpaRepository<HouseholdMember, Long> {
    public HouseholdMember findHouseholdMembersByUser(Long userId);
    public HouseholdMember findHouseholdMembersByHousehold(Long householdId);
    public HouseholdMember save(HouseholdMember householdMember);
    boolean existsById(HouseholdMemberKey householdMemberKey);
}
