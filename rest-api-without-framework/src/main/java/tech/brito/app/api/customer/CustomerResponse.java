package tech.brito.app.api.customer;

import java.util.UUID;

public class CustomerResponse {

    private UUID id;

    public CustomerResponse(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
