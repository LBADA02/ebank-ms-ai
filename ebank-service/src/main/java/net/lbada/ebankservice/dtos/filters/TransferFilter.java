package net.lbada.ebankservice.dtos.filters;

import net.lbada.ebankservice.enums.StatusFather;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferFilter(
        String destinationAccountId,
        String beneficiaryId,
        StatusFather status,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        BigDecimal minAmount,
        BigDecimal maxAmount)
{}
