package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.HouseholdMemberAlreadyExistsException;
import no.freshify.api.exception.HouseholdNotFoundException;
import no.freshify.api.exception.UserNotFoundException;
import no.freshify.api.model.*;
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
    @Autowired
    private final HouseholdMemberService householdMemberService;
    @Autowired
    private final HouseholdService householdService;
    @Autowired
    private final UserService userService;

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
}
