package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.model.Household;
import no.freshify.api.exception.UserNotFoundException;
import no.freshify.api.model.User;
import no.freshify.api.model.dto.UserFull;
import no.freshify.api.service.HouseholdService;
import no.freshify.api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final HouseholdService householdService;

    //TODO Remember to add authentication logic and verify/enforce proper access privileges before processing request
    /**
     * Gets a user by id
     * @param id The id of the user to find
     * @return The found user
     * @throws UserNotFoundException If the user is not found
     */
    @GetMapping("/{id}")
    public UserFull getUserById(@PathVariable long id) throws UserNotFoundException {
        User user = userService.getUserById(id);

        return new UserFull(user);
    }

    //TODO Remember to add authentication logic and verify/enforce proper access privileges before processing request
    /**
     * Gets the households that a given user is part of
     * @param userId The user to find households from
     * @return A list of found households
     */
    @GetMapping("/{id}/households")
    public ResponseEntity<List<Household>> getHouseholds(@PathVariable("id") long userId) {
        return ResponseEntity.ok(householdService.getHouseholds(userId));
    }
}
