package tech.brito.app.api.account;

import tech.brito.enums.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

import static java.util.Objects.nonNull;

public class AccountRequest {

    private UUID customerId;
    private Integer agency;
    private BigDecimal withdrawalFee;
    private AccountType type;

    public boolean isHasAgency() {
        return nonNull(agency);
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public Integer getAgency() {
        return agency;
    }

    public void setAgency(Integer agency) {
        this.agency = agency;
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
}
