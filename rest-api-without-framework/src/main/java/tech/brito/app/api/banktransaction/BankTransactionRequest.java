package tech.brito.app.api.banktransaction;

import java.math.BigDecimal;
import java.util.UUID;

public class BankTransactionRequest {

    private BigDecimal value;
    private UUID destinationAccountId;

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public UUID getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(UUID destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }
}
