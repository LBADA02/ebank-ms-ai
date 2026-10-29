package net.lbada.ebankservice.controllers;


import net.lbada.ebankservice.entities.BankAccount;
import net.lbada.ebankservice.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("accounts")
public class EbankAccountRestController {

    private BankAccountService bankAccountService;

    public EbankAccountRestController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping()
    public List<BankAccount> getAllBankAccounts(){
        return bankAccountService.getAllBankAccounts();
    }

    @GetMapping("/{id}")
    public BankAccount getBankAccountById(@PathVariable String id){
        return bankAccountService.getBankAccountById(id);
    }

    @PostMapping()
    public BankAccount saveBankAccount(@RequestBody BankAccount bankAccount){
        return bankAccountService.saveBankAccount(bankAccount);
    }

}
