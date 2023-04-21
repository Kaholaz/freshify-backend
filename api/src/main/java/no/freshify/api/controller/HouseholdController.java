package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.HouseholdNotFoundException;
import no.freshify.api.exception.InvalidHouseholdMemberRoleException;
import no.freshify.api.exception.UserDoesNotBelongToHouseholdException;
import no.freshify.api.exception.UserNotFoundException;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.UserTypeRequest;
import no.freshify.api.repository.HouseholdMemberRepository;
import no.freshify.api.repository.HouseholdRepository;
import no.freshify.api.repository.UserRepository;
import no.freshify.api.service.HouseholdMemberService;
import no.freshify.api.service.HouseholdService;

import no.freshify.api.service.UserService;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.InvalidRoleValueException;
import java.util.List;
import java.util.Optional;

@RequestMapping("/household")
@RequiredArgsConstructor
@RestController
public class HouseholdController {
    private final HouseholdService householdService;
    private final HouseholdMemberService householdMemberService;
    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(HouseholdController.class);
    private final HouseholdRepository householdRepository;
    private final HouseholdMemberRepository householdMemberRepository;
    private final UserRepository userRepository;


    /**
     * Creates a new household.
     * @param household The new household
     * @return The new household
     */
    @PostMapping()
    public ResponseEntity<Household> createHousehold(@RequestBody Household household) {
        logger.info("Creating household");
        Household savedHousehold = householdRepository.save(household);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHousehold);
    }

    /**
     * Deletes a household
     * @param householdId The household to delete
     * @return
     * @throws HouseholdNotFoundException If the household was not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteHousehold(@PathVariable("id") long householdId) throws HouseholdNotFoundException {
        logger.info("Deleting household");
        if (!householdRepository.existsById(householdId)) {
            logger.warn("Household not found");
            throw new HouseholdNotFoundException();
        }
        householdRepository.deleteById(householdId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets a household by id
     * @param householdId The id for the household to find
     * @return A household with given id
     * @throws HouseholdNotFoundException If household with given id is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Household> getHouseholdById(@PathVariable("id") long householdId) throws HouseholdNotFoundException {
        return ResponseEntity.ok(householdService.getHousehold(householdId));
    }

    //TODO Remember to add authentication logic and verify/enforce access privileges before processing request
    /**
     * Gets the users in a given household
     * @param householdId The household to get usres from
     * @return A list of users in the given household
     */
    @GetMapping("/{id}/users")
    public ResponseEntity<List<User>> getUsers(@PathVariable("id") long householdId) throws HouseholdNotFoundException {
        return ResponseEntity.ok(householdService.getUsers(householdId));
    }


    /**
     * Updates the attributes of a given household.
     * @param householdId The id of the household to update
     * @param household The new household
     * @return Household representing updated version
     * @throws HouseholdNotFoundException If household was not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Household> updateHousehold(@PathVariable("id") long householdId, @RequestBody Household household)
            throws HouseholdNotFoundException {
        Optional<Household> householdData = householdRepository.findById(householdId);

        if (householdData.isPresent()) {
            Household _household = householdData.get();
            _household.setName(household.getName());
            return ResponseEntity.ok(householdRepository.save(_household));
        } else {
            logger.warn("Household not found");
            throw new HouseholdNotFoundException();
        }
    }

    /**
     * Updates the type of a given user within a given household.
     * @param householdId The household where the user type is updated
     * @param userTypeRequest The new user type
     * @return HouseholdMember representing the new
     * @throws HouseholdNotFoundException If the household is not found
     * @throws UserNotFoundException If the user is not found inside given household
     */
    @PutMapping("/{id}/users")
    public ResponseEntity<HouseholdMember> updateUserType(@PathVariable("id") long householdId,
                                                          @RequestBody UserTypeRequest userTypeRequest)
            throws HouseholdNotFoundException, UserNotFoundException, InvalidRoleValueException, UserDoesNotBelongToHouseholdException, InvalidHouseholdMemberRoleException {
        logger.info("Updating user type");
        Household household = householdService.findHouseholdByHouseholdId(householdId);
        User user = userService.getUserById(userTypeRequest.getUserId());

        HouseholdMemberKey householdMemberKey = new HouseholdMemberKey(user.getId(), household.getId());

        HouseholdMember userInHousehold = householdMemberService.getHouseholdMemberByHouseholdMemberKey(householdMemberKey);

        HouseholdMemberRole newRole;
        try {
            newRole = HouseholdMemberRole.valueOf(userTypeRequest.getUserType());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid role: " + userTypeRequest.getUserType());
            throw new InvalidHouseholdMemberRoleException();
        }

        userInHousehold.setRole(newRole);
        return ResponseEntity.ok(householdMemberRepository.save(userInHousehold));
    }
}