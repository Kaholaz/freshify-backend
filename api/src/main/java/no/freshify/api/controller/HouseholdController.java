package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.HouseholdNotFoundException;
import no.freshify.api.model.Household;
import no.freshify.api.model.User;
import no.freshify.api.repository.HouseholdRepository;
import no.freshify.api.service.HouseholdService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/household")
@RequiredArgsConstructor
@RestController
public class HouseholdController {
    private final HouseholdService householdService;

    private final Logger logger = LoggerFactory.getLogger(HouseholdController.class);
    private final HouseholdRepository householdRepository;


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
    public ResponseEntity<Household> updateHousehold(@PathVariable("id") long householdId, @RequestBody Household household) throws HouseholdNotFoundException {
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
}