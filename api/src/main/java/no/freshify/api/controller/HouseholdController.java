package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.*;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.UserFull;
import no.freshify.api.service.HouseholdMemberService;
import no.freshify.api.service.HouseholdService;

import no.freshify.api.service.UserService;
import org.apache.coyote.Response;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final Logger logger = LoggerFactory.getLogger(HouseholdController.class);


    /**
     * Creates a new household. Sets the logged on user as a superuser in the new household
     * @param household The new household
     * @return The new household
     */
    @PreAuthorize("#user == authentication.principal")
    @PostMapping()
    public ResponseEntity<Household> createHousehold(@RequestBody Household household,
                                                     @AuthenticationPrincipal User user)
            throws HouseholdMemberAlreadyExistsException {
        ResponseEntity<Household> response = householdService.addHousehold(household);

        householdMemberService.addHouseholdMember(
                new HouseholdMember(
                        new HouseholdMemberKey(household.getId(), user.getId()),
                        household, user, HouseholdMemberRole.SUPERUSER));

        return response;
    }

    /**
     * Deletes a household. Can only be done by a superuser.
     * @param householdId The household to delete
     * @return
     * @throws HouseholdNotFoundException If the household was not found
     */
    @PreAuthorize("hasPermission(#householdId, 'Household', 'SUPERUSER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteHousehold(@PathVariable("id") long householdId) throws HouseholdNotFoundException {
        long idToDelete = householdService.findHouseholdByHouseholdId(householdId).getId();
        return householdService.removeHousehold(idToDelete);
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
    public ResponseEntity<List<UserFull>> getUsers(@PathVariable("id") long householdId) throws HouseholdNotFoundException {
        return ResponseEntity.ok(householdService.getUsers(householdId));
    }


    /**
     * Updates the attributes of a given household. Can only be done by superuser
     * @param householdId The id of the household to update
     * @param household The new household
     * @return Household representing updated version
     * @throws HouseholdNotFoundException If household was not found
     */
    @PreAuthorize("hasPermission(#householdId, 'Household', 'SUPERUSER')")
    @PutMapping("/{id}")
    public ResponseEntity<Household> updateHousehold(@PathVariable("id") long householdId, @RequestBody Household household)
            throws HouseholdNotFoundException {
        Household _household = householdService.findHouseholdByHouseholdId(householdId);

        _household.setName(_household.getName());

        return householdService.addHousehold(_household);
    }
}