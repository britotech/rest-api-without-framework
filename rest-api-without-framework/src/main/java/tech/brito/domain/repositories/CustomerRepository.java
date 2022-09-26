package tech.brito.domain.repositories;

import tech.brito.domain.models.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository {

    Customer create(Customer customer);

    Customer findById(UUID id);

    List<Customer> findAll();

}
