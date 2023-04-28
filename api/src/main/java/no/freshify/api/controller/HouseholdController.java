package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.*;
import no.freshify.api.model.*;
import no.freshify.api.model.dto.CreateHousehold;
import no.freshify.api.model.dto.HouseholdDTO;
import no.freshify.api.model.dto.HouseholdMemberDTO;
import no.freshify.api.model.mapper.HouseholdMapper;
import no.freshify.api.model.mapper.HouseholdMemberMapper;
import no.freshify.api.security.AuthenticationService;
import no.freshify.api.service.HouseholdService;

import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequestMapping("/household")
@RequiredArgsConstructor
@RestController
public class HouseholdController {
    private final HouseholdService householdService;
    private final AuthenticationService authenticationService;

    private final HouseholdMapper householdMapper = Mappers.getMapper(HouseholdMapper.class);
    private final HouseholdMemberMapper householdMemberMapper = Mappers.getMapper(HouseholdMemberMapper.class);
    private final Logger logger = LoggerFactory.getLogger(HouseholdMemberController.class);

    /**
     * Creates a new household. Sets the logged on user as a superuser in the new household.
     * @param household The new household
     * @return The new household
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping()
    public ResponseEntity<HouseholdDTO> createHousehold(@RequestBody CreateHousehold household) {
        User loggedInUser = authenticationService.getLoggedInUser();
        Household _household = new Household();
        _household.setName(household.getName());

        Set<HouseholdMember> members = new HashSet<>();
        // Add logged in user as superuser in the new household
        members.add(
                new HouseholdMember(
                        new HouseholdMemberKey(_household.getId(), loggedInUser.getId()),
                        _household, loggedInUser, HouseholdMemberRole.SUPERUSER)
        );
        _household.setHouseholdMembers(members);

        HouseholdDTO createdDTO = householdMapper.toHouseholdDTO(householdService.addHousehold(_household));

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDTO);
    }

    /**
     * Deletes a household. Can only be done by a superuser.
     * @param householdId The household to delete
     * @return
     * @throws HouseholdNotFoundException If the household was not found
     */
    @PreAuthorize("hasPermission(#householdId, 'Household', 'SUPERUSER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHousehold(@PathVariable("id") long householdId) throws HouseholdNotFoundException {
        long idToDelete = householdService.findHouseholdByHouseholdId(householdId).getId();
        householdService.removeHousehold(idToDelete);
        logger.info("Removed household");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Operation successful");
    }

    /**
     * Gets a household by id
     * @param householdId The id for the household to find
     * @return A household with given id
     * @throws HouseholdNotFoundException If household with given id is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<HouseholdDTO> getHouseholdById(@PathVariable("id") long householdId) throws HouseholdNotFoundException {
        return ResponseEntity.ok(householdMapper.toHouseholdDTO(householdService.findHouseholdByHouseholdId(householdId)));
    }

    //TODO Remember to add authentication logic and verify/enforce access privileges before processing request
    /**
     * Gets the users in a given household
     * @param householdId The household to get users from
     * @return A list of users in the given household
     */
    @GetMapping("/{id}/users")
    public ResponseEntity<List<HouseholdMemberDTO>> getUsers(@PathVariable("id") long householdId)
            throws HouseholdNotFoundException {
        return ResponseEntity.ok(householdMemberMapper.householdMemberDTOS(householdService.getUsers(householdId)));
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
    public ResponseEntity<HttpStatus> updateHousehold(@PathVariable("id") long householdId, @RequestBody HouseholdDTO household)
            throws HouseholdNotFoundException {
        Household _household = householdService.findHouseholdByHouseholdId(householdId);
        _household.setName(household.getName());

        householdService.updateHousehold(_household);
        return ResponseEntity.ok().build();
    }
}