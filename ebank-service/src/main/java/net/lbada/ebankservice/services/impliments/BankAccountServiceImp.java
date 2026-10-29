package net.lbada.ebankservice.services.impliments;

import net.lbada.ebankservice.entities.BankAccount;
import net.lbada.ebankservice.repositories.BankAccountRepository;
import net.lbada.ebankservice.services.BankAccountService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
public class BankAccountServiceImp implements BankAccountService {

    private BankAccountRepository bankAccountRepository;

    public BankAccountServiceImp(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll();
    }

    @Override
    public BankAccount getBankAccountById(String id) {
        return bankAccountRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Bank Account not found")
        );
    }

    @Override
    public BankAccount saveBankAccount(BankAccount bankAccount) {
        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setCreateAt(new Date());
        return bankAccountRepository.save(bankAccount);
    }
}
