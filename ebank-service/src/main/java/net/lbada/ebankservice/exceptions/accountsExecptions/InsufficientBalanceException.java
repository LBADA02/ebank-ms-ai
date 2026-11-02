package net.lbada.ebankservice.exceptions.accountsExecptions;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
