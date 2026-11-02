package net.lbada.ebankservice.mappers;


import lombok.RequiredArgsConstructor;
import net.lbada.ebankservice.dtos.TransferDto;
import net.lbada.ebankservice.entities.Transfer;
import net.lbada.ebankservice.models.Customer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TransferMapper {

    public TransferDto toDto(
            Transfer transfer,
            Customer sourceCustomer,
            Customer destinationCustomer
    ) {
        return new TransferDto(
                transfer.getId(),
                transfer.getSourceAccount().getId(),
                sourceCustomer,
                transfer.getDestinationAccount().getId(),
                destinationCustomer,
                transfer.getAmount(),
                transfer.getDescription(),
                transfer.getStatus(),
                transfer.getCreatedAt()
        );
    }
}