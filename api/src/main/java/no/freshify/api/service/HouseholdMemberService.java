package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.model.HouseholdMember;
import no.freshify.api.model.HouseholdMemberKey;
import no.freshify.api.repository.HouseholdMemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HouseholdMemberService {
    private final HouseholdMemberRepository householdMemberRepository;

    public void addHouseholdMember(HouseholdMember householdMember) {
        householdMemberRepository.saveAndFlush(householdMember);
    }

    public boolean householdMemberExists(Long householdId, Long userId) {
        HouseholdMemberKey householdMemberKey = new HouseholdMemberKey(householdId, userId);
        return householdMemberRepository.existsById(householdMemberKey);
    }
}
