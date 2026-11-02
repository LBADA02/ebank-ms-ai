package net.lbada.ebankservice.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.lbada.ebankservice.enums.StatusFather;
import net.lbada.ebankservice.enums.TransactionTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "transfers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_transfer_idempotency",
                        columnNames = "idempotency_key"
                )
        }
)
@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "source_account_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    private BankAccount sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "destination_account_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    private BankAccount destinationAccount;


    // Optional: transfer can happen without a saved beneficiary
    private String beneficiaryId;

    private BigDecimal amount	; //BigDecimal	Montant

    private String description ; //	String	Motif du virement
    private StatusFather status ;

    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey ; //Éviter les doubles exécutions

    private LocalDateTime createdAt;

    //executedAt	LocalDateTime	Date d'exécution
}
