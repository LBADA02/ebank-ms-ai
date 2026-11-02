package net.lbada.ebankservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.lbada.ebankservice.exceptions.accountsExecptions.InsufficientBalanceException;
import net.lbada.ebankservice.models.Customer;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@AllArgsConstructor @NoArgsConstructor @Data  @Builder
public class BankAccount {
    @Id
    private String id;
    private Date createAt;
    private BigDecimal balance;
    private String type;
    private Long customerId;

    @Transient
    private Customer customer;


    public void validationSufficientBalance(BigDecimal balance)
    {
        if (this.balance.compareTo(balance) < 0)
        {
            throw new InsufficientBalanceException(
                    "Insufficient account balance"
            );
        }
    }

    public void debit(BigDecimal amount) {
        validationSufficientBalance(amount);
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

}
