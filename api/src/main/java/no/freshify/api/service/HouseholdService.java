package no.freshify.api.service;

import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.HouseholdMemberAlreadyExistsException;
import no.freshify.api.exception.HouseholdNotFoundException;
import no.freshify.api.exception.UserNotFoundException;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.UserFull;
import no.freshify.api.repository.HouseholdMemberRepository;
import no.freshify.api.repository.HouseholdRepository;
import no.freshify.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseholdService {

    private final HouseholdMemberService householdMemberService;

    private final HouseholdRepository householdRepository;
    private final HouseholdMemberRepository householdMemberRepository;
    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(HouseholdService.class);

    public List<UserFull> getUsers(long householdId) throws HouseholdNotFoundException {
        logger.info("Getting users in household with id: " + householdId);
        if (!householdRepository.existsById(householdId)) {
            logger.warn("Household with id " + householdId + " not found");
            throw new HouseholdNotFoundException();
        }

        List<HouseholdMember> householdMembers = householdMemberRepository.findHouseholdMembersByHouseholdId(householdId);
        List<UserFull> users = new ArrayList<>();
        for (HouseholdMember householdMember : householdMembers) {
            User user = householdMember.getUser();
            UserFull userFull = new UserFull(user.getId(), user.getFirstName(), user.getEmail());
            users.add(userFull);
        }

        return users;
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

    public Household findHouseholdByHouseholdId(Long householdId) throws HouseholdNotFoundException {
        Household household = householdRepository.findHouseholdById(householdId);
        if (household == null) {
            logger.warn("Household not found");
            throw new HouseholdNotFoundException();
        }
        return household;
    }

    public Household getHousehold(long householdId) throws HouseholdNotFoundException {
        Household household = householdRepository.findHouseholdById(householdId);
        if (household == null) {
            logger.warn("Household not found");
            throw new HouseholdNotFoundException();
        } else {
            return household;
        }
    }

    public Household addHousehold(Household household) {
        logger.info("Creating household");
        Household savedHousehold = householdRepository.save(household);
        return savedHousehold;
    }

    public ResponseEntity<HttpStatus> removeHousehold(long householdId) throws HouseholdNotFoundException {
        logger.info("Deleting household");
        if (!householdRepository.existsById(householdId)) {
            logger.warn("A household with given id does not exist");
            throw new HouseholdNotFoundException();
        }

        householdRepository.deleteById(householdId);
        return ResponseEntity.noContent().build();
    }

    public void updateHousehold(Household household) {
        logger.info("Updating household");
        householdRepository.save(household);
    }
}