package tech.brito.domain.repositories;

import tech.brito.domain.models.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepositoryImpl implements UserRepository {

    private static final Map<String, User> USERS_STORE = new ConcurrentHashMap();

    @Override
    public User create(User user) {
        user.setId(UUID.randomUUID());
        USERS_STORE.put(user.getUsername(), user);
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return USERS_STORE.get(username);
    }
}
