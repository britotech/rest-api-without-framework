package tech.brito.app;

import com.sun.net.httpserver.BasicAuthenticator;
import tech.brito.app.api.user.PasswordEncoder;
import tech.brito.domain.UserService;

public class ApiAuthenticator extends BasicAuthenticator {

    private final UserService userService;

    public ApiAuthenticator(UserService userService) {
        super("realm");
        this.userService = userService;
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        var optionalUser = userService.findByUsername(username);
        return optionalUser.isPresent() && optionalUser.get().getPassword().equals(PasswordEncoder.encode(password));
    }
}
