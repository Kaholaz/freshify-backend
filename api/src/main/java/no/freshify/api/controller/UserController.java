package no.freshify.api.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import no.freshify.api.exception.UserNotFoundException;

import no.freshify.api.model.User;
import no.freshify.api.model.dto.*;
import no.freshify.api.model.mapper.HouseholdMapper;
import no.freshify.api.model.mapper.UserMapper;
import no.freshify.api.model.mapper.UserMapperImpl;

import no.freshify.api.security.AuthenticationService;
import no.freshify.api.security.CookieFactory;
import no.freshify.api.security.UserAuthentication;
import no.freshify.api.security.UserDetailsImpl;

import no.freshify.api.service.HouseholdService;
import no.freshify.api.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final HouseholdService householdService;
    private final UserMapper userMapper = new UserMapperImpl();
    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;
    private final HouseholdMapper householdMapper = Mappers.getMapper(HouseholdMapper.class);

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody CreateUser user, HttpServletResponse response) {
        logger.info("Creating user: " + user.getEmail());
        if (userService.getUserByEmail(user.getEmail()) != null) {
            logger.warn("User already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }

        User userFromDatabase = userService.createUser(userMapper.fromCreateUser(user));
        logger.info("User created: " + user.getEmail());

        Authentication auth = new UserAuthentication(new UserDetailsImpl(userFromDatabase, List.of()));
        String jwt = authenticationService.generateToken(auth);
        Cookie cookie = CookieFactory.getAuthorizationCookie(jwt);

        response.addCookie(cookie);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toUserFull(userFromDatabase));
    }

    @GetMapping
    public ResponseEntity<Object> getUserByEmail(@RequestParam String email) throws UserNotFoundException {
        logger.info("Getting user by email: " + email);
        User user = userService.getUserByEmail(email);
        if (user == null) {
            logger.warn("User not found");
            throw new UserNotFoundException();
        }

        logger.info("User found, returning user");
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toUserId(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletResponse response) {
        logger.info("Logging out user");
        Cookie cookie = CookieFactory.getAuthorizationCookie("");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        logger.info("User logged out");
        return ResponseEntity.status(HttpStatus.OK).body("Logged out");
    }

    @PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || #updateRequest.id == authentication.principal.id)")
    @PutMapping
    public ResponseEntity<UserFull> updateUser(@RequestBody UpdateUser updateRequest) throws UserNotFoundException {
        logger.info("Updating user: " + updateRequest.getId());

        User user = userService.getUserById(updateRequest.getId());
        user.setFirstName(updateRequest.getFirstName());
        user.setEmail(updateRequest.getEmail());

        if (updateRequest.getPassword() != null && !updateRequest.getPassword().equals("")) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        userService.updateUser(user);
        logger.info("User updated");
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toUserFull(user));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginUser user, HttpServletResponse response) {
        Authentication authentication;
        logger.info("Logging in user: " + user.getEmail());

        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        } catch (Exception e) {
            logger.warn("Incorrect user credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect user credentials");
        }
        String jwt = authenticationService.generateToken(authentication);
        response.addCookie(CookieFactory.getAuthorizationCookie(jwt));

        User userFromDb = userService.getUserByEmail(user.getEmail());
        logger.info("User logged in");
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toUserFull(userFromDb));
    }

    /**
     * Gets a user by id, admins can access everyone while users are restricted to themselves
     * @param userId The id of the user to find
     * @return The found user
     * @throws UserNotFoundException If the user is not found
     */
    @PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || #userId == authentication.principal.id)")
    @GetMapping("/{id}")
    public UserFull getUserById(@PathVariable("id") long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws UserNotFoundException {
        logger.info("Getting user by id: " + userId);
        User user = userService.getUserById(userDetails.getId());
        logger.info("User found, returning user");
        return userMapper.toUserFull(user);
    }

    /**
     * Gets the households that a given user is part of, admins can access everything while users are restricted to themselves
     * @param userId The user to find households from
     * @return A list of found households
     */
    @PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || #userId == authentication.principal.id)")
    @GetMapping("/{id}/households")
    public ResponseEntity<List<HouseholdDTO>> getHouseholds(@PathVariable("id") long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws UserNotFoundException {
        logger.info("Getting households for user: " + userId);
        return ResponseEntity.ok(householdMapper.toHouseholdDTO(householdService.getHouseholds(userDetails.getId())));
    }

    @PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || #userId == authentication.principal.id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws UserNotFoundException {
        logger.info("Deleting user: " + userId);
        userService.deleteUser(userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body("User deleted");
    }

    /**
     * Gets the logged in user
     * @param userDetails The logged in user details
     * @return The details of the logged in user
     * @throws UserNotFoundException If the user is not found
     */
    @PreAuthorize("authentication.principal.id != null")
    @GetMapping("/loggedin")
    public ResponseEntity<UserFull> getLoggedInUser(@AuthenticationPrincipal UserDetailsImpl userDetails) throws UserNotFoundException {
        logger.info("Getting logged in user");
        User user = userService.getUserById(userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toUserFull(user));
    }
}
