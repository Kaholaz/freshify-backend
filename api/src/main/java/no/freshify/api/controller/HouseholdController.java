package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.model.Household;
import no.freshify.api.model.User;
import no.freshify.api.service.HouseholdService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/household")
@RequiredArgsConstructor
@RestController
public class HouseholdController {
    private final HouseholdService householdService;


    /**
     * Gets the users in a given household
     * @param householdId The household to get usres from
     * @return A list of users in the given household
     */
    @GetMapping("/{id}/users")
    public ResponseEntity<List<User>> getUsers(@PathVariable("id") long householdId) {
        return ResponseEntity.ok(householdService.getUsers(householdId));
    }
}