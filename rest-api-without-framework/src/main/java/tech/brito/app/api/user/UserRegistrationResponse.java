package tech.brito.app.api.user;


import java.util.UUID;

public class UserRegistrationResponse {

    private UUID id;
    private String username;

    public UserRegistrationResponse(UUID id, String username) {
        this.id = id;
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}