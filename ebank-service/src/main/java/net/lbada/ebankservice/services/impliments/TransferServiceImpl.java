package net.lbada.ebankservice.services.impliments;

import net.lbada.ebankservice.dtos.TransferDto;
import net.lbada.ebankservice.dtos.filters.TransferFilter;
import net.lbada.ebankservice.dtos.requests.TransferRequest;
import net.lbada.ebankservice.entities.BankAccount;
import net.lbada.ebankservice.entities.Transfer;
import net.lbada.ebankservice.enums.StatusFather;
import net.lbada.ebankservice.exceptions.accountsExecptions.AccountNotFoundException;
import net.lbada.ebankservice.mappers.TransferMapper;
import net.lbada.ebankservice.models.Customer;
import net.lbada.ebankservice.repositories.BankAccountRepository;
import net.lbada.ebankservice.repositories.TransferRepository;
import net.lbada.ebankservice.restClient.CustomerRestClient;
import net.lbada.ebankservice.services.CustomerGateway;
import net.lbada.ebankservice.services.TransactionService;
import net.lbada.ebankservice.services.TransferService;
import net.lbada.ebankservice.specification.TransferSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class TransferServiceImpl implements TransferService {

    private TransferRepository transferRepository ;
    private BankAccountRepository bankAccountRepository;
    private TransferMapper transferMapper;
    private TransactionService transactionService;
    //private CustomerRestClient customerRestClient;
    private CustomerGateway customerGateway;


    public TransferServiceImpl(TransferRepository transferRepository, BankAccountRepository bankAccountRepository, TransferMapper transferMapper, TransactionService transactionService, CustomerRestClient customerRestClient, CustomerGateway customerGateway) {
        this.transferRepository = transferRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.transferMapper = transferMapper;
        this.transactionService = transactionService;
        this.customerGateway = customerGateway;

    }


    @Override
    @Transactional
    public TransferDto makeTransfer(TransferRequest request ,
                                            String idempotencyKey
    ) {

        // 0. Check idempotency
        Optional<Transfer> existingTransfer =
                transferRepository.findByIdempotencyKey(idempotencyKey);

        if (existingTransfer.isPresent()) {
            Transfer transfer = existingTransfer.get();

            Customer sourceCustomer = customerGateway.getCustomerById(
                    transfer.getSourceAccount().getCustomerId()
            );

            Customer destinationCustomer = customerGateway.getCustomerById(
                    transfer.getDestinationAccount().getCustomerId()
            );

            return transferMapper.toDto(
                    transfer,
                    sourceCustomer,
                    destinationCustomer
            );
        }
// 2. Retrieve accounts
        BankAccount sourceAccount = bankAccountRepository
                .findById(request.sourceAccountId())
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Source account not found"
                        )
                );

        BankAccount destinationAccount = bankAccountRepository
                .findById(request.destinationAccountId())
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Destination account not found"
                        )
                );

        // 3. Validate
        if (sourceAccount.getId()
                .equals(destinationAccount.getId())) {

            throw new IllegalArgumentException(
                    "Impossible de faire un virement au même compte"
            );
        }

        sourceAccount.validationSufficientBalance(request.amount());

        // 4. Execute balance movement
        sourceAccount.debit(request.amount());
        destinationAccount.credit(request.amount());

        // 5. Save transfer
        Transfer savedTransfer = saveNewTransfer(
                sourceAccount,
                destinationAccount,
                request,
                idempotencyKey
        );

        // 6. Record transactions
        transactionService.recordTransfer(savedTransfer);

        // 7. Get customers in ONE batch call
        Map<Long, Customer> customers = getCustomers(
                sourceAccount.getCustomerId(),
                destinationAccount.getCustomerId()
        );

        // 8. Return DTO
        return transferMapper.toDto(
                savedTransfer,
                customers.get(sourceAccount.getCustomerId()),
                customers.get(destinationAccount.getCustomerId())
        );

    }

    private Transfer saveNewTransfer(BankAccount sourceAccount,BankAccount destinationAccount,TransferRequest request,String idempotencyKey){
        Transfer transfer = new Transfer();
        transfer.setSourceAccount(sourceAccount);
        transfer.setDestinationAccount(destinationAccount);
        transfer.setBeneficiaryId(request.beneficiaryId());
        transfer.setAmount(request.amount());
        transfer.setDescription(request.description());
        transfer.setStatus(StatusFather.COMPLETED);
        transfer.setCreatedAt(LocalDateTime.now());

        transfer.setIdempotencyKey(idempotencyKey);

        Transfer savedTransfer = transferRepository.save(transfer);

        return savedTransfer;
    }

    @Override
    public TransferDto getTransfer(String transferId) {
       Transfer transfer = transferRepository.findById(transferId).orElseThrow(
               () -> new AccountNotFoundException("Destination account not found")
       );

        // 3. Extract all distinct customer IDs from joined accounts
        // 2. Get the two customer IDs
        Set<Long> customerIds = Stream.of(
                        transfer.getSourceAccount(),
                        transfer.getDestinationAccount()
                )
                .filter(Objects::nonNull)
                .map(BankAccount::getCustomerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 4. ONE batch call to Customer Service
        Map<Long, Customer> customers = customerGateway
                .getCustomersByIds(new ArrayList<>(customerIds))
                .stream()
                .collect(Collectors.toMap(
                        Customer::getId,
                        Function.identity()
                ));

        // 4. Get source and destination customers
        Customer sourceCustomer = customers.get(
                transfer.getSourceAccount().getCustomerId()
        );

        Customer destinationCustomer = customers.get(
                transfer.getDestinationAccount().getCustomerId()
        );


        return transferMapper.toDto(
                transfer,
                sourceCustomer,
                destinationCustomer
        );
    }

    @Override
    public Page<TransferDto> getCustomerTransfers( TransferFilter filter,
                                                   Pageable pageable) {
        //Transfer -> AccountId -> idConsumer

        // Get authenticated customer's ID
        //Long customerId = securityService.getCurrentCustomerId();
        Long customerId = 2L;

        List<String> accountIds = bankAccountRepository
                .findByCustomerId(customerId)
                .stream()
                .map(BankAccount::getId)
                .toList();

        if (accountIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Specification<Transfer> specification =
                TransferSpecification.filter(accountIds, filter);

        Page<Transfer> transfers =
                transferRepository.findAll(specification, pageable);

        // 3. Extract all distinct customer IDs from joined accounts
        Set<Long> customerIds = transfers.getContent()
                .stream()
                .flatMap(transfer -> Stream.of(
                        transfer.getSourceAccount(),
                        transfer.getDestinationAccount()
                ))
                .filter(Objects::nonNull)
                .map(BankAccount::getCustomerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (customerIds.isEmpty()) {
            return transfers.map(transfer ->
                    transferMapper.toDto(transfer, null, null)
            );
        }

        // 4. ONE batch call to Customer Service
        Map<Long, Customer> customers = customerGateway
                .getCustomersByIds(new ArrayList<>(customerIds))
                .stream()
                .collect(Collectors.toMap(
                        Customer::getId,
                        Function.identity()
                ));

        // 5. Map transfers to DTO
        return transfers.map(transfer -> {

            Customer sourceCustomer = customers.get(
                    transfer.getSourceAccount().getCustomerId()
            );

            Customer destinationCustomer = customers.get(
                    transfer.getDestinationAccount().getCustomerId()
            );

            return transferMapper.toDto(
                    transfer,
                    sourceCustomer,
                    destinationCustomer
            );
        });
    }

    private Map<Long, Customer> getCustomers(
            Long sourceCustomerId,
            Long destinationCustomerId
    ) {
        Set<Long> customerIds = Stream.of(
                        sourceCustomerId,
                        destinationCustomerId
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return customerGateway
                .getCustomersByIds(new ArrayList<>(customerIds))
                .stream()
                .collect(Collectors.toMap(
                        Customer::getId,
                        Function.identity()
                ));
    }


    @Override
    public StatusFather getTransferStatus(String transferId) {
        return null;
    }
}
