package net.lbada.ebankservice.services.impliments;

import net.lbada.ebankservice.entities.BankAccount;
import net.lbada.ebankservice.models.Customer;
import net.lbada.ebankservice.repositories.BankAccountRepository;
import net.lbada.ebankservice.restClient.CustomerRestClient;
import net.lbada.ebankservice.services.BankAccountService;
import net.lbada.ebankservice.services.CustomerGateway;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
public class BankAccountServiceImp implements BankAccountService {

    private BankAccountRepository bankAccountRepository;
    private CustomerRestClient customerRestClient;
    private CustomerGateway customerGateway;

    public BankAccountServiceImp(CustomerRestClient customerRestClient,BankAccountRepository bankAccountRepository, CustomerGateway customerGateway) {
        this.bankAccountRepository = bankAccountRepository;
        this.customerGateway = customerGateway;
        this.customerRestClient = customerRestClient;
    }

    @Override
    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll();
    }

    @Override
    public BankAccount getBankAccountById(String id) {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Bank Account not found")
        );
        Customer customer =customerGateway.getCustomerById(bankAccount.getCustomerId());
        bankAccount.setCustomer(customer);
        return bankAccount;

    }

    @Override
    public BankAccount saveBankAccount(BankAccount bankAccount) {
        try {
            Customer customer = customerRestClient.getCustomerById(bankAccount.getCustomerId());
            bankAccount.setId(UUID.randomUUID().toString());
            bankAccount.setCreateAt(new Date());
            return bankAccountRepository.save(bankAccount);
        }catch (Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }



    }
}
