package tech.brito.domain.models;

import tech.brito.enums.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public abstract class AccountImpl implements Account {

    protected static final Integer AGENCY_DEFAULT = 395;

    protected UUID id;

    protected final Customer customer;
    protected final Integer agency;
    protected final Integer number;
    protected BigDecimal balance;

    public AccountImpl(Customer customer) {
        this.customer = customer;
        this.number = generateAccountNumber();
        this.agency = AGENCY_DEFAULT;
        this.balance = BigDecimal.ZERO;
    }

    public AccountImpl(Customer customer, Integer agency) {
        this.customer = customer;
        this.agency = agency;
        this.number = generateAccountNumber();
        this.balance = BigDecimal.ZERO;
    }

    protected abstract Integer generateAccountNumber();

    public abstract AccountType getAccountType();

    @Override
    public void deposit(BigDecimal value) {
        balance = balance.add(value);
    }

    @Override
    public void transfer(BigDecimal value, Account destinationAccount) {
        withdraw(value);
        destinationAccount.deposit(value);
    }

    public UUID getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getAgency() {
        return agency;
    }

    public Integer getNumber() {
        return number;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
