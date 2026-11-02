package net.lbada.ebankservice.controllers;


import jakarta.validation.Valid;
import net.lbada.ebankservice.dtos.TransferDto;
import net.lbada.ebankservice.dtos.filters.TransferFilter;
import net.lbada.ebankservice.dtos.requests.TransferRequest;
import net.lbada.ebankservice.services.TransferService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfer")
public class TransferRestController {
    private TransferService transferService;

    public TransferRestController(TransferService transferService) {
        this.transferService = transferService;
    }


    // Create a transfer
    @PostMapping
    public ResponseEntity<TransferDto> makeTransfer(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody TransferRequest request
    ) {
        TransferDto transfer = transferService.makeTransfer(request , idempotencyKey);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transfer);
    }
    /*
    {
      "sourceAccountId": "ACC-1001",
      "destinationAccountId": "ACC-2001",
      "beneficiaryId": null,
      "amount": 500,
      "description": "Family support"
    }
     */


    @GetMapping
    public Page<TransferDto> getCustomerTransfers(
            @ModelAttribute TransferFilter filter,
            @PageableDefault(size = 10, sort = "createdAt",
                    direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return transferService.getCustomerTransfers(filter, pageable);
    }
    /*
    {
      "content": [],
      "totalElements": 45,
      "totalPages": 5,
      "size": 10,
      "number": 0
    }
     */



}
