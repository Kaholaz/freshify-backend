package no.freshify.api.service;

import no.freshify.api.exception.UserNotFoundException;
import no.freshify.api.model.User;
import no.freshify.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private final Long userId = 1L;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(userId);
    }

    @Test
    public void testGetUserById() throws UserNotFoundException {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserById(userId);

        assertEquals(user, result);
    }

    @Test
    public void testGetUserByIdThrowsException() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(userId);
        });
    }

    @Test
    public void testGetUserByEmail() {
        String email = "test";

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail(email);

        assertEquals(user, result);
    }

    @Test
    public void testCreateUser() {
        Mockito.when(userRepository.saveAndFlush(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals(user, result);
    }

    @Test
    public void testUpdateUser() {
        Mockito.when(userRepository.saveAndFlush(user)).thenReturn(user);

        User result = userService.updateUser(user);

        assertEquals(user, result);
    }

    @Test
    public void testDeleteUser() throws UserNotFoundException {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.deleteUser(userId);

        assertEquals(user, result);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
    }

    @Test
    public void testDeleteUserThrowsException() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });

        Mockito.verify(userRepository, Mockito.never()).deleteById(userId);
    }
}