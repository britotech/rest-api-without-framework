package tech.brito.domain.models;

import tech.brito.enums.AccountType;

import java.math.BigDecimal;

public final class SavingsAccount extends AccountImpl {

    private static Integer SEQUENCE = 10000;

    private BigDecimal withdrawalFee;

    public SavingsAccount(Customer customer, BigDecimal withdrawalFee) {
        super(customer);
        this.withdrawalFee = withdrawalFee;
    }

    public SavingsAccount(Customer customer, Integer agency, BigDecimal withdrawalFee) {
        super(customer, agency);
        this.withdrawalFee = withdrawalFee;
    }

    @Override
    protected Integer generateAccountNumber() {
        SEQUENCE++;
        return SEQUENCE;
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.SAVINGS_ACCOUNT;
    }

    @Override
    public void withdraw(BigDecimal value) {
        balance = balance.subtract(value.add(withdrawalFee));
    }

    public BigDecimal getWithdrawalFee() {
        return withdrawalFee;
    }
}
