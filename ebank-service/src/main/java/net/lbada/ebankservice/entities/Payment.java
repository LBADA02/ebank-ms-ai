package net.lbada.ebankservice.entities;


import jakarta.persistence.*;
import net.lbada.ebankservice.enums.PaymentType;
import net.lbada.ebankservice.enums.StatusFather;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payment_idempotency",
                        columnNames = "idempotency_key"
                )
        }
)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Account from which money is debited
    @Column(nullable = false)
    private UUID accountId;

    // Provider from billing-service
    private UUID providerId;

    // Invoice from billing-service (optional for some payment types)
    private UUID invoiceId;

    // Customer's reference at the provider
    private String beneficiaryReference;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    private PaymentType paymentType;


    private StatusFather status;

    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    private String description;

    private LocalDateTime createdAt;
}
