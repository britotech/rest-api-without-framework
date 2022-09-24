package tech.brito.data;

import tech.brito.domain.User;
import tech.brito.domain.UserRepository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {

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
