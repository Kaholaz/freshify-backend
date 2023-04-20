package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.model.Household;
import no.freshify.api.model.HouseholdMember;
import no.freshify.api.model.User;
import no.freshify.api.repository.HouseholdMemberRepository;
import no.freshify.api.repository.HouseholdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseholdService {

    private final HouseholdRepository householdRepository;
    private final HouseholdMemberRepository householdMemberRepository;

    public List<User> getUsers(long householdId) {
        return householdMemberRepository.findHouseholdMembersByHousehold(householdId)
                .stream()
                .map(HouseholdMember::getUser)
                .collect(Collectors.toList());
    }

    public List<Household> getHouseholds(long userId) {
        return householdMemberRepository.findHouseholdMembersByUser(userId)
                .stream()
                .map(HouseholdMember::getHousehold)
                .collect(Collectors.toList());
    }
}