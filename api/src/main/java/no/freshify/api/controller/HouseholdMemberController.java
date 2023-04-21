package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.*;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.HouseholdMemberDTO;
import no.freshify.api.model.dto.UserTypeRequest;
import no.freshify.api.model.mapper.HouseholdMemberMapper;
import no.freshify.api.repository.HouseholdMemberRepository;
import no.freshify.api.service.HouseholdMemberService;
import no.freshify.api.service.HouseholdService;
import no.freshify.api.service.UserService;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/household")
@RequiredArgsConstructor
public class HouseholdMemberController {

    private final HouseholdMemberService householdMemberService;
    private final HouseholdService householdService;
    private final UserService userService;
    private final HouseholdMemberMapper householdMemberMapper = Mappers.getMapper(HouseholdMemberMapper.class);

    private final Logger logger = LoggerFactory.getLogger(HouseholdMemberController.class);

    /**
     * Adds a user to a household
     * @param id The id of the household to add the user to
     * @param requestBody The request body containing the id of the user to add
     * @return A response entity containing the result of the operation
     * @throws UserNotFoundException If the user is not found
     * @throws HouseholdNotFoundException If the household is not found
     * @throws HouseholdMemberAlreadyExistsException If the user is already a member of the household
     */
    @PreAuthorize("hasPermission(#id, 'Household', 'SUPERUSER')")
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
     * Updates the household member role of a given user within a given household.
     * @param householdId The household where the user role is updated
     * @param userTypeRequest The new user role
     * @return HouseholdMember object with updated role
     * @throws HouseholdNotFoundException If the household is not found
     * @throws UserNotFoundException If the user is not found inside given household
     * @throws UserDoesNotBelongToHouseholdException If the user is not a member of the household
     * @throws InvalidHouseholdMemberRoleException If the new user role is invalid
     */
    @PreAuthorize("hasPermission(#id, 'Household', 'SUPERUSER')")
    @PutMapping("/{id}/users")
    public ResponseEntity<HouseholdMemberDTO> updateHouseholdMemberRole(@PathVariable("id") long householdId,
                                                                        @RequestBody UserTypeRequest userTypeRequest)
            throws HouseholdNotFoundException, UserNotFoundException, UserDoesNotBelongToHouseholdException, InvalidHouseholdMemberRoleException {
        logger.info("Updating user type");
        Household household = householdService.findHouseholdByHouseholdId(householdId);
        User user = userService.getUserById(userTypeRequest.getUserId());

        HouseholdMemberKey householdMemberKey = new HouseholdMemberKey(user.getId(), household.getId());

        HouseholdMember userInHousehold = householdMemberService.getHouseholdMemberByHouseholdMemberKey(householdMemberKey);

        try {
            userInHousehold.setRole(HouseholdMemberRole.valueOf(userTypeRequest.getUserType()));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid role: " + userTypeRequest.getUserType());
            throw new InvalidHouseholdMemberRoleException();
        }

        householdMemberService.updateHouseholdMember(userInHousehold);

        return ResponseEntity.ok(householdMemberMapper.toHouseholdMemberDTO(userInHousehold));
    }
}
