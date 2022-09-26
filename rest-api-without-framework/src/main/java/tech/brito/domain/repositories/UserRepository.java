package tech.brito.domain.repositories;

import tech.brito.domain.models.User;

public interface UserRepository {

    User create(User user);

    User findByUsername(String username);
}
