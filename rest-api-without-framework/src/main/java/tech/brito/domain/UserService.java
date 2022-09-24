package tech.brito.domain;

import tech.brito.app.api.user.PasswordEncoder;

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
