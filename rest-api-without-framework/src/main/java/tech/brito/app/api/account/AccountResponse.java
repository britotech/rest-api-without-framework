package tech.brito.app.api.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import tech.brito.app.api.customer.CustomerResponse;
import tech.brito.enums.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponse {

    private UUID id;

    private CustomerResponse customer;
    private Integer agency;
    private Integer number;
    private BigDecimal balance;
    private BigDecimal withdrawalFee;
    private AccountType type;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getAgency() {
        return agency;
    }

    public void setAgency(Integer agency) {
        this.agency = agency;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getWithdrawalFee() {
        return withdrawalFee;
    }

    public void setWithdrawalFee(BigDecimal withdrawalFee) {
        this.withdrawalFee = withdrawalFee;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public CustomerResponse getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerResponse customer) {
        this.customer = customer;
    }
}
