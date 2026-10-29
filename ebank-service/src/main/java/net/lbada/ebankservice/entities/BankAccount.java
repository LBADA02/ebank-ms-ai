package net.lbada.ebankservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.lbada.ebankservice.models.Customer;

import java.util.Date;

@Entity
@AllArgsConstructor @NoArgsConstructor @Data  @Builder
public class BankAccount {
    @Id
    private String id;
    private Date createAt;
    private double balance;
    private String type;
    private Long customerId;

    @Transient
    private Customer customer;
}
