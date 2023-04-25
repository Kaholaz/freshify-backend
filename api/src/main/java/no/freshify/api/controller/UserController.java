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
import no.freshify.api.security.UserDetailsImpl;
import no.freshify.api.service.HouseholdService;
import no.freshify.api.service.UserService;
import org.mapstruct.factory.Mappers;
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


    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody CreateUser user) {
        userService.createUser(userMapper.fromCreateUser(user));
        return ResponseEntity.status(HttpStatus.CREATED).body("User created");
    }

    @GetMapping
    public ResponseEntity<Object> getUserByEmail(@RequestParam String email) throws UserNotFoundException {
        User user = userService.getUserByEmail(email);
        if (user == null) throw new UserNotFoundException();
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toUserId(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletResponse response) {
        Cookie cookie = CookieFactory.getAuthorizationCookie("");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.status(HttpStatus.OK).body("Logged out");
    }

    @PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || #updateRequest.id == authentication.principal.id)")
    @PutMapping
    public ResponseEntity<UserFull> updateUser(@RequestBody UpdateUser updateRequest) throws UserNotFoundException {
        User user = userService.getUserById(updateRequest.getId());
        user.setFirstName(updateRequest.getFirstName());
        user.setEmail(updateRequest.getEmail());

        if (updateRequest.getPassword() != null && !updateRequest.getPassword().equals("")) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        userService.updateUser(user);

        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toUserFull(user));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginUser user, HttpServletResponse response) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong password");
        }

        String jwt = authenticationService.generateToken(authentication);
        response.addCookie(CookieFactory.getAuthorizationCookie(jwt));

        User userFromDb = userService.getUserByEmail(user.getEmail());
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
        User user = userService.getUserById(userDetails.getId());
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
        return ResponseEntity.ok(householdMapper.toHouseholdDTO(householdService.getHouseholds(userDetails.getId())));
    }

    @PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || #userId == authentication.principal.id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws UserNotFoundException {
        userService.deleteUser(userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body("User deleted");
    }
}
