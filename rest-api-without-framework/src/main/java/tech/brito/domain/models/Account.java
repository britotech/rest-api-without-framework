package tech.brito.domain.models;

import java.math.BigDecimal;

public interface Account {

    void withdraw(BigDecimal value);

    void deposit(BigDecimal value);

    void transfer(BigDecimal value, Account destinationAccount);
}
