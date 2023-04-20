package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.exception.UserNotFoundException;
import no.freshify.api.model.User;
import no.freshify.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    public User getUserById(long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            logger.info("User not found");
            throw new UserNotFoundException();
        }

        return user;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
