package tech.brito.domain.repositories;

import tech.brito.domain.models.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class CustomerRepositoryImpl implements CustomerRepository {

    private static final Map<UUID, Customer> CUSTOMERS_STORE = new ConcurrentHashMap();

    @Override
    public Customer create(Customer customer) {

        if(isNull(customer.getId())){
            customer.setId(UUID.randomUUID());
        }

        CUSTOMERS_STORE.put(customer.getId(), customer);
        return customer;
    }

    @Override
    public Customer findById(UUID id) {
        return CUSTOMERS_STORE.get(id);
    }

    @Override
    public List<Customer> findAll() {
        return CUSTOMERS_STORE.values().stream().collect(Collectors.toList());
    }
}
