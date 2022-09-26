package tech.brito.domain.models;

import java.util.Objects;
import java.util.UUID;

public class Customer {

    private UUID id;
    private String name;
    private String lastName;

    public Customer(String name, String lastName) {
        this.name = name;
        this.lastName = lastName;
    }

    public Customer(UUID id, String name, String lastName) {
        this(name, lastName);
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (Objects.isNull(o) || getClass() != o.getClass()) {
            return false;
        }

        Customer customer = (Customer) o;
        return id.equals(customer.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
