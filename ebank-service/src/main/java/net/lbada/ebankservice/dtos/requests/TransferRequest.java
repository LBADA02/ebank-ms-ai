package net.lbada.ebankservice.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public record TransferRequest(

        @NotBlank
        String sourceAccountId,

        String beneficiaryId,

        String destinationAccountId,

        @NotNull
        @Positive
        BigDecimal amount,

        @NotBlank
        String currency,

        @Size(max = 255)
        String description

) {}


/***

 {
     "sourceAccountId": "ACCOUNT-1001",
     "beneficiaryId": "BENEFICIARY-001",
     "amount": 1000,
     "currency": "MAD",
     "description": "Family support"
 }
 Transfer without a saved beneficiary:
 {
     "sourceAccountId": "ACCOUNT-1001",
     "destinationAccountId": "ACCOUNT-2001",
     "amount": 1000,
     "currency": "MAD",
     "description": "Family support"
 }

 */
