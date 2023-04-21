package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.HouseholdNotFoundException;
import no.freshify.api.model.Household;
import no.freshify.api.model.HouseholdMember;
import no.freshify.api.model.User;
import no.freshify.api.repository.HouseholdMemberRepository;
import no.freshify.api.repository.HouseholdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseholdService {

    private final HouseholdRepository householdRepository;
    private final HouseholdMemberRepository householdMemberRepository;

    private final Logger logger = LoggerFactory.getLogger(HouseholdService.class);

    public List<User> getUsers(long householdId) {
        return householdMemberRepository.findHouseholdMembersByHouseholdId(householdId)
                .stream()
                .map(HouseholdMember::getUser).toList();
    }

    public List<Household> getHouseholds(long userId) {
        return householdMemberRepository.findHouseholdMembersByUserId(userId)
                .stream()
                .map(HouseholdMember::getHousehold)
                .toList();
    }

    public Household findHouseholdByHouseholdId(Long householdId) throws HouseholdNotFoundException {
        Household household = householdRepository.findHouseholdById(householdId);
        if (household == null) {
            logger.info("Household not found");
            throw new HouseholdNotFoundException();
        }
        return household;
    }
}