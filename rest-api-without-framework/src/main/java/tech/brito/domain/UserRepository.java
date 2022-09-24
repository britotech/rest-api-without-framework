package tech.brito.domain;

public interface UserRepository {

    User create(User user);

    User findByUsername(String username);
}
