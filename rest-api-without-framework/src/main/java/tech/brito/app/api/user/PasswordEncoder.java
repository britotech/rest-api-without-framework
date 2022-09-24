package tech.brito.app.api.user;

import java.util.Base64;

public class PasswordEncoder {
    public static String encode(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }
}
