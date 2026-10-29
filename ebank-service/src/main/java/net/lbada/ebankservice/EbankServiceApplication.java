package net.lbada.ebankservice;

import net.lbada.ebankservice.entities.BankAccount;
import net.lbada.ebankservice.enums.BankAccountTypes;
import net.lbada.ebankservice.services.impliments.BankAccountServiceImp;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class EbankServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankServiceApplication.class, args);
    }


    @Bean
    CommandLineRunner commandLineRunner(BankAccountServiceImp bankAccountServiceImp)
    {
        return args -> {
            for (int i=1 ; i <= 3 ; i++)
            {
                for(int j=0 ; j<5 ; j++)
                {
                    bankAccountServiceImp.saveBankAccount(BankAccount.builder()
                                    .type(String.valueOf(Math.random()>0.5? BankAccountTypes.CURENT_ACCOUNT : BankAccountTypes.SAVING_ACCOUNT))
                                    .balance(1000 + Math.random()*5000)
                                    .customerId((long) i)
                            .build());
                }
            }
        };
    }
}
