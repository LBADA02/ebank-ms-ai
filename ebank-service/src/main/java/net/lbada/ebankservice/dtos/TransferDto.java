package net.lbada.ebankservice.dtos;

import net.lbada.ebankservice.enums.StatusFather;
import net.lbada.ebankservice.models.Customer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferDto(
        String id,
        String sourceAccountId,
        Customer sourceCustomer,
        String destinationAccountId,
        Customer destinationCustomer,
        //String beneficiaryId,
        BigDecimal amount,
        String description,
        StatusFather status,
        LocalDateTime createdAt
) {}
