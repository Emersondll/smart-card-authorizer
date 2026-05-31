package io.github.emersondll.transactions.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import io.github.emersondll.transactions.config.constants.RabbitMqConstants;
import io.github.emersondll.transactions.document.AccountDocument;
import io.github.emersondll.transactions.document.OperationsTypeDocument;
import io.github.emersondll.transactions.document.TransactionsDocument;
import io.github.emersondll.transactions.exception.InvalidTransactionException;
import io.github.emersondll.transactions.mapper.TransactionMapper;
import io.github.emersondll.transactions.model.request.TransactionsRequest;
import io.github.emersondll.transactions.model.response.BalanceResponse;
import io.github.emersondll.transactions.model.response.TransactionsResponse;
import io.github.emersondll.transactions.repository.TransactionsRepository;
import io.github.emersondll.transactions.service.AccountService;
import io.github.emersondll.transactions.service.OperationsTypeService;
import io.github.emersondll.transactions.service.RabbitMqService;
import io.github.emersondll.transactions.service.TransactionsService;
import lombok.extern.slf4j.Slf4j;

/**
 * Business logic implementation for financial transaction operations.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Validating incoming transaction requests (required fields).</li>
 *   <li>Verifying the referenced account and operation type exist.</li>
 *   <li>Normalising the amount sign based on the operation type (purchases/withdrawals
 *       must be negative; payments must be positive).</li>
 *   <li>Persisting the transaction document in MongoDB.</li>
 *   <li>Publishing a domain event to the appropriate RabbitMQ queue.</li>
 *   <li>Computing account balance as the algebraic sum of all transactions.</li>
 * </ul>
 *
 * <p>Thread Safety: Stateless service; thread-safe as a Spring singleton.</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see AccountService for account look-up
 * @see OperationsTypeService for operation type look-up
 * @see TransactionMapper for DTO conversions
 * @see RabbitMqService for event publishing
 */
@Service
@Slf4j
public class TransactionsServiceImpl implements TransactionsService {

    private static final String NEW_TRANSACTION_PREFIX = "New transaction registered with ID ";
    private static final String TYPE_SUFFIX = " and operation type: ";
    private static final String VALUE_SUFFIX = " and value: ";

    private final AccountService accountService;
    private final OperationsTypeService typeService;
    private final TransactionsRepository repository;
    private final TransactionMapper mapper;
    private final RabbitMqService mqService;

    /**
     * Constructor-based dependency injection.
     *
     * @param accountService service for account validation (non-null)
     * @param typeService    service for operation type look-up (non-null)
     * @param repository     repository for transaction persistence (non-null)
     * @param mapper         mapper for DTO conversions (non-null)
     * @param mqService      service for publishing domain events (non-null)
     * @throws NullPointerException if any dependency is null
     */
    public TransactionsServiceImpl(AccountService accountService,
                                   OperationsTypeService typeService,
                                   TransactionsRepository repository,
                                   TransactionMapper mapper,
                                   RabbitMqService mqService) {
        this.accountService = Objects.requireNonNull(accountService, "AccountService cannot be null");
        this.typeService = Objects.requireNonNull(typeService, "OperationsTypeService cannot be null");
        this.repository = Objects.requireNonNull(repository, "TransactionsRepository cannot be null");
        this.mapper = Objects.requireNonNull(mapper, "TransactionMapper cannot be null");
        this.mqService = Objects.requireNonNull(mqService, "RabbitMqService cannot be null");
    }

    /**
     * {@inheritDoc}
     *
     * @throws InvalidTransactionException if amount, operationTypeId, or accountId is missing
     */
    @Override
    public TransactionsResponse createTransaction(TransactionsRequest request) {
        Objects.requireNonNull(request, "TransactionsRequest cannot be null");
        validateRequiredFields(request);
        log.info("Creating transaction. accountId={}, operationTypeId={}",
                request.accountId(), request.operationTypeId());

        OperationsTypeDocument typeDocument = typeService.findById(request.operationTypeId());
        accountService.findById(request.accountId());

        TransactionsRequest normalised = normaliseAmountSign(request, typeDocument);
        TransactionsDocument saved = repository.save(mapper.requestToDocument(normalised));
        String queueName = resolveQueue(typeDocument);

        mqService.send(queueName, buildEventMessage(saved, request));
        log.info("Transaction created. transactionId={}", saved.getTransactionsId());

        return mapper.documentToResponse(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BalanceResponse recoveryBalance(String documentNumber) {
        Objects.requireNonNull(documentNumber, "Document number cannot be null");
        log.info("Computing balance. documentNumber={}", documentNumber);

        AccountDocument account = accountService.findByDocumentNumber(documentNumber);
        List<TransactionsDocument> transactions = repository.findAllByAccountId(account.getAccountId());

        BigDecimal balance = transactions.stream()
                .map(TransactionsDocument::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Balance computed. documentNumber={}, balance={}", documentNumber, balance);
        return new BalanceResponse(balance);
    }

    /**
     * Returns a new {@link TransactionsRequest} with the amount sign normalised according
     * to the operation type.
     *
     * <ul>
     *   <li>Payments ({@code PAGAMENTO}): amount must be positive (negate if negative).</li>
     *   <li>Purchases/withdrawals: amount must be negative (negate if positive).</li>
     * </ul>
     *
     * @param request      the original request (non-null)
     * @param typeDocument the resolved operation type document (non-null)
     * @return a new immutable {@link TransactionsRequest} with the corrected amount
     */
    protected TransactionsRequest normaliseAmountSign(TransactionsRequest request,
                                                      OperationsTypeDocument typeDocument) {
        log.debug("Normalising amount sign for operationType={}", typeDocument.getDescription());

        if (isPayment(typeDocument)) {
            return ensurePositive(request);
        }
        return ensureNegative(request);
    }

    /**
     * Resolves the RabbitMQ queue name for the given operation type.
     *
     * @param typeDocument the operation type document (non-null)
     * @return the RabbitMQ queue name constant
     */
    protected String resolveQueue(OperationsTypeDocument typeDocument) {
        if (isPayment(typeDocument)) {
            return RabbitMqConstants.PAYMENT;
        }
        if (isWithdrawal(typeDocument)) {
            return RabbitMqConstants.WITHDRAWAL;
        }
        return RabbitMqConstants.PURCHASE;
    }

    // ---- private helpers ----

    private void validateRequiredFields(TransactionsRequest request) {
        if (request.amount() == null
                || request.operationTypeId() == null
                || request.accountId() == null) {
            throw new InvalidTransactionException("Amount, operationTypeId and accountId are required");
        }
    }

    private boolean isPayment(OperationsTypeDocument typeDocument) {
        return typeDocument.getDescription().contains("PAGAMENTO");
    }

    private boolean isWithdrawal(OperationsTypeDocument typeDocument) {
        return typeDocument.getDescription().contains("SAQUE");
    }

    private TransactionsRequest ensurePositive(TransactionsRequest request) {
        if (request.amount().signum() < 0) {
            return request.withAmount(request.amount().negate());
        }
        return request;
    }

    private TransactionsRequest ensureNegative(TransactionsRequest request) {
        if (request.amount().signum() > 0) {
            return request.withAmount(request.amount().negate());
        }
        return request;
    }

    private String buildEventMessage(TransactionsDocument saved, TransactionsRequest request) {
        return NEW_TRANSACTION_PREFIX + saved.getTransactionsId()
                + TYPE_SUFFIX + request.operationTypeId()
                + VALUE_SUFFIX + request.amount();
    }
}
