package net.lbada.ebankservice.services.impliments;

import net.lbada.ebankservice.entities.Transaction;
import net.lbada.ebankservice.entities.Transfer;
import net.lbada.ebankservice.enums.OperationType;
import net.lbada.ebankservice.enums.TransactionTypes;
import net.lbada.ebankservice.repositories.TransactionRepository;
import net.lbada.ebankservice.services.TransactionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    @Override
    public void recordTransfer(Transfer transfer) {
        // Debit transaction: sender
        Transaction debit = new Transaction();
        debit.setAccountId(transfer.getSourceAccountId());
        debit.setTransactionTypes(TransactionTypes.DEBIT);
        debit.setOperationType( OperationType.TRANSFER);
        debit.setAmount(transfer.getAmount());
        debit.setOperationId(transfer.getId());
        debit.setDescription("Transfer sent: " + transfer.getDescription());
        debit.setCreatedAt(LocalDateTime.now());

        // Credit transaction: receiver
        Transaction credit = new Transaction();
        credit.setAccountId(transfer.getDestinationAccountId());
        credit.setTransactionTypes(TransactionTypes.CREDIT);
        credit.setOperationType(OperationType.TRANSFER);
        credit.setAmount(transfer.getAmount());
        credit.setOperationId(transfer.getId());
        credit.setDescription("Transfer received: " + transfer.getDescription());
        credit.setCreatedAt(LocalDateTime.now());

        transactionRepository.saveAll(List.of(debit, credit));
    }
}
