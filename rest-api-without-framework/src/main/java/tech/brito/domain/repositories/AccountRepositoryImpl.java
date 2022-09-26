package tech.brito.domain.repositories;

import tech.brito.domain.models.AccountImpl;
import tech.brito.domain.models.Customer;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class AccountRepositoryImpl implements AccountRepository {

    private static final Map<UUID, AccountImpl> ACCOUNTS_STORE = new ConcurrentHashMap();

    @Override
    public AccountImpl save(AccountImpl account) {

        if (isNull(account.getId())) {
            account.setId(UUID.randomUUID());
        }

        ACCOUNTS_STORE.put(account.getId(), account);
        return account;
    }

    public AccountImpl findById(UUID id) {
        return ACCOUNTS_STORE.get(id);
    }

    @Override
    public List<AccountImpl> findByCustomer(Customer customer) {
        var accounts = ACCOUNTS_STORE.values().stream().filter(a -> a.getCustomer().equals(customer)).collect(Collectors.toList());
        return accounts;
    }
}
