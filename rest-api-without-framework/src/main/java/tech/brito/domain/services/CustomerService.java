package tech.brito.domain.services;


import tech.brito.domain.models.Customer;
import tech.brito.domain.repositories.CustomerRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer create(Customer customer) {
        return customerRepository.create(customer);
    }

    public Optional<Customer> findById(UUID id) {
        var customer = customerRepository.findById(id);
        if (Objects.isNull(customer)) {
            return Optional.empty();
        }

        return Optional.of(customer);
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
}
