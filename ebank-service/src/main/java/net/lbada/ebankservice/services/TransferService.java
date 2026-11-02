package net.lbada.ebankservice.services;

import net.lbada.ebankservice.dtos.TransferDto;
import net.lbada.ebankservice.dtos.filters.TransferFilter;
import net.lbada.ebankservice.dtos.requests.TransferRequest;
import net.lbada.ebankservice.enums.StatusFather;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TransferService {

    // Initiate a transfer
    TransferDto makeTransfer(TransferRequest request , String idempotencyKey);

    // Get transfer details
    TransferDto getTransfer(String transferId);

    // Get customer's transfer history
    Page<TransferDto> getCustomerTransfers(TransferFilter filter,
                                           Pageable pageable); //Mabe later adding pagable

    // Get transfer status
    StatusFather getTransferStatus(String transferId);



    // Confirm a pending transfer
    //TransferDto confirmTransfer(UUID transferId);

    // Cancel a pending transfer
    //Transfer cancelTransfer(UUID transferId);




}
