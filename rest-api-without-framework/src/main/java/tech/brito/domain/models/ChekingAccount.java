package tech.brito.domain.models;

import tech.brito.enums.AccountType;

import java.math.BigDecimal;

public final class ChekingAccount extends AccountImpl {

    private static Integer SEQUENCE = 30000;

    public ChekingAccount(Customer customer) {
        super(customer);
    }

    public ChekingAccount(Customer customer, Integer agency) {
        super(customer, agency);
    }

    @Override
    protected Integer generateAccountNumber() {
        SEQUENCE++;
        return SEQUENCE;
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.CHEKING_ACCOUNT;
    }

    @Override
    public void withdraw(BigDecimal value) {
        balance = balance.subtract(value);
    }
}
