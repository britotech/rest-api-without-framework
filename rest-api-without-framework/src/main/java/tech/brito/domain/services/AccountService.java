package tech.brito.domain.services;

import tech.brito.domain.models.AccountImpl;
import tech.brito.domain.models.Customer;
import tech.brito.domain.repositories.AccountRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountImpl save(AccountImpl account) {
        return accountRepository.save(account);
    }

    public Optional<AccountImpl> findById(UUID id) {
        var account = accountRepository.findById(id);
        if (isNull(account)) {
            return Optional.empty();
        }

        return Optional.of(account);
    }

    public List<AccountImpl> findByCustomer(Customer customer) {
        return accountRepository.findByCustomer(customer);
    }

    public void withdraw(AccountImpl account, BigDecimal value) {
        account.withdraw(value);
        save(account);
    }

    public void deposit(AccountImpl account, BigDecimal value) {
        account.deposit(value);
        save(account);
    }

    public void transfer(AccountImpl account, BigDecimal value, AccountImpl destinationAccount) {
        account.transfer(value, destinationAccount);
        save(account);
        save(destinationAccount);
    }
}
