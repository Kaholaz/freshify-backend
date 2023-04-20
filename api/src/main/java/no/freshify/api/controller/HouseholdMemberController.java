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

    @PostMapping("{id}/add")
    public ResponseEntity<String> addUser(@PathVariable Long id, @RequestBody Map<String, Long> requestBody) throws UserNotFoundException, HouseholdNotFoundException, HouseholdMemberAlreadyExistsException {
        Long userId = requestBody.get("userId");

        logger.info("Adding user with id: " + userId + " to household with id: " + id);

        User user = userService.getUserById(userId);
        if (user == null) {
            logger.warn("User not found");
            throw new UserNotFoundException();
        }

        Household household = householdService.findHouseholdByHouseholdId(id);
        if (household == null) {
            logger.warn("Household not found");
            throw new HouseholdNotFoundException();
        }

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
