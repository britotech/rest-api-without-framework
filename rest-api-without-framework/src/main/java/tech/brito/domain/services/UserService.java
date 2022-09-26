package tech.brito.domain.services;

import tech.brito.domain.models.User;
import tech.brito.domain.repositories.UserRepository;

import java.util.Objects;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User user) {
        return userRepository.create(user);
    }

    public Optional<User> findByUsername(String username) {
        var user = userRepository.findByUsername(username);
        if (Objects.isNull(user)) {
            return Optional.empty();
        }

        return Optional.of(user);
    }
}
