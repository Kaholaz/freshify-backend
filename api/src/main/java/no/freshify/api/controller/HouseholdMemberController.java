package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.*;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.UserTypeRequest;
import no.freshify.api.repository.HouseholdMemberRepository;
import no.freshify.api.service.HouseholdMemberService;
import no.freshify.api.service.HouseholdService;
import no.freshify.api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/household")
@RequiredArgsConstructor
public class HouseholdMemberController {

    private final HouseholdMemberService householdMemberService;
    private final HouseholdService householdService;
    private final UserService userService;
    private final HouseholdMemberRepository householdMemberRepository;

    private final Logger logger = LoggerFactory.getLogger(HouseholdMemberController.class);

    //TODO Remember to add authentication logic and verify access privileges before processing request
    /**
     * Adds a user to a household
     * @param id The id of the household to add the user to
     * @param requestBody The request body containing the id of the user to add
     * @return A response entity containing the result of the operation
     * @throws UserNotFoundException If the user is not found
     * @throws HouseholdNotFoundException If the household is not found
     * @throws HouseholdMemberAlreadyExistsException If the user is already a member of the household
     */
    @PostMapping("{id}/add")
    public ResponseEntity<String> addUser(@PathVariable Long id, @RequestBody Map<String, Long> requestBody) throws UserNotFoundException, HouseholdNotFoundException, HouseholdMemberAlreadyExistsException {
        Long userId = requestBody.get("userId");

        logger.info("Adding user with id: " + userId + " to household with id: " + id);

        User user = userService.getUserById(userId);

        Household household = householdService.findHouseholdByHouseholdId(id);

        HouseholdMember householdMember = new HouseholdMember();
        householdMember.setUser(user);
        householdMember.setHousehold(household);
        householdMember.setRole(HouseholdMemberRole.USER);
        householdMember.setId(new HouseholdMemberKey(household.getId(), user.getId()));

        householdMemberService.addHouseholdMember(householdMember);
        logger.info("Added household member");
        return ResponseEntity.ok("Operation successful");
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
    public ResponseEntity<HouseholdMember> updateHouseholdMemberRole(@PathVariable("id") long householdId,
                                                          @RequestBody UserTypeRequest userTypeRequest)
            throws HouseholdNotFoundException, UserNotFoundException, UserDoesNotBelongToHouseholdException, InvalidHouseholdMemberRoleException {
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
