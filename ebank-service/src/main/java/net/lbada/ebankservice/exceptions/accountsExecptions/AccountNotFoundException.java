package net.lbada.ebankservice.exceptions.accountsExecptions;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
