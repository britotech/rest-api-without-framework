package tech.brito.domain.repositories;

import tech.brito.domain.models.AccountImpl;
import tech.brito.domain.models.Customer;

import java.util.List;
import java.util.UUID;

public interface AccountRepository {

    AccountImpl save(AccountImpl account);

    AccountImpl findById(UUID id);

    List<AccountImpl> findByCustomer(Customer customer);
}
