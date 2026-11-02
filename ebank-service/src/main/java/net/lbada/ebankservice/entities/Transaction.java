package net.lbada.ebankservice.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.lbada.ebankservice.enums.OperationType;
import net.lbada.ebankservice.enums.TransactionTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id ;
    private String accountId ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionTypes transactionTypes ; //Enum	DEBIT ou CREDIT
    private BigDecimal amount	 ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType operationType;
    private String operationId; //UUID	ID du Transfer ou Payment

    private String description ; //	String	Description

    private BigDecimal balanceAfter ; //BigDecimal	Solde après le mouvement
    private LocalDateTime createdAt ; //	LocalDateTime	Date d'enregistrement
}
