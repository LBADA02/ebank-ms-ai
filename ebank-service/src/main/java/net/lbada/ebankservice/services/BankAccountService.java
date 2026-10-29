package net.lbada.ebankservice.services;

import net.lbada.ebankservice.entities.BankAccount;

import java.util.List;

public interface BankAccountService {

    public List<BankAccount> getAllBankAccounts();
    public BankAccount getBankAccountById(String id);
    public BankAccount saveBankAccount(BankAccount bankAccount);
}
