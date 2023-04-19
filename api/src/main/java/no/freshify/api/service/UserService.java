package no.freshify.api.service;

import lombok.RequiredArgsConstructor;
import no.freshify.api.model.User;
import no.freshify.api.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }
}
