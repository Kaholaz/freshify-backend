package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.HouseholdNotFoundException;
import no.freshify.api.exception.UserNotFoundException;
import no.freshify.api.model.Household;
import no.freshify.api.model.HouseholdMember;
import no.freshify.api.model.User;
import no.freshify.api.repository.HouseholdMemberRepository;
import no.freshify.api.repository.HouseholdRepository;
import no.freshify.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseholdService {

    private final HouseholdRepository householdRepository;
    private final HouseholdMemberRepository householdMemberRepository;
    private final UserRepository userRepository;
    Logger logger = LoggerFactory.getLogger(HouseholdService.class);

    public List<User> getUsers(long householdId) throws HouseholdNotFoundException {
        logger.info("Getting users in household with id: " + householdId);
        if (!householdRepository.existsById(householdId)) {
            logger.warn("Household with id " + householdId + " not found");
            throw new HouseholdNotFoundException();
        }

        return householdMemberRepository.findHouseholdMembersByHouseholdId(householdId)
                .stream()
                .map(HouseholdMember::getUser).toList();
    }

    public List<Household> getHouseholds(long userId) throws UserNotFoundException {
        logger.info("Getting households of user with id: " + userId);
        if (!userRepository.existsById(userId)) {
            logger.warn("User with id " + userId + " not found");
            throw new UserNotFoundException();
        }

        return householdMemberRepository.findHouseholdMembersByUserId(userId)
                .stream()
                .map(HouseholdMember::getHousehold)
                .toList();
    }

    public Household findHouseholdByHouseholdId(Long householdId) {
        return householdRepository.findHouseholdById(householdId);
    }
}