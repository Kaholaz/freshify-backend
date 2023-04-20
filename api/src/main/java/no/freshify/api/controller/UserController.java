package no.freshify.api.controller;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.UserNotFoundException;
import no.freshify.api.model.User;
import no.freshify.api.model.dto.UserFull;
import no.freshify.api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserFull getUserById(@PathVariable long id) throws UserNotFoundException {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return new UserFull(user);
    }
}
