package tech.brito.enums;

public enum AccountType {
    CHEKING_ACCOUNT, SAVINGS_ACCOUNT;

    public boolean isSavingsAccount() {
        return SAVINGS_ACCOUNT.equals(this);
    }
}
