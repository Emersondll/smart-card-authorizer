package io.github.emersondll.transactions.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.emersondll.transactions.config.constants.RabbitMqConstants;
import io.github.emersondll.transactions.document.AccountDocument;
import io.github.emersondll.transactions.exception.AccountNotFoundException;
import io.github.emersondll.transactions.mapper.AccountMapper;
import io.github.emersondll.transactions.model.request.AccountRequest;
import io.github.emersondll.transactions.model.response.AccountDetailResponse;
import io.github.emersondll.transactions.model.response.AccountResponse;
import io.github.emersondll.transactions.repository.AccountRepository;
import io.github.emersondll.transactions.service.AccountService;
import io.github.emersondll.transactions.service.RabbitMqService;
import lombok.extern.slf4j.Slf4j;

/**
 * Business logic implementation for account management.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Idempotent account creation: returns the existing account if the document
 *       number is already registered, otherwise creates and persists a new one.</li>
 *   <li>Account retrieval by ID with proper not-found handling.</li>
 *   <li>Publishing domain events to RabbitMQ when a new account is created.</li>
 * </ul>
 *
 * <p>Thread Safety: Stateless service; thread-safe as a Spring singleton.</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see AccountRepository for persistence operations
 * @see AccountMapper for DTO conversions
 * @see RabbitMqService for event publishing
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;
    private final AccountMapper mapper;
    private final RabbitMqService mqService;

    /**
     * Constructor-based dependency injection.
     *
     * @param repository repository for account persistence operations (non-null)
     * @param mapper     mapper for converting between request/document/response (non-null)
     * @param mqService  service for publishing domain events to RabbitMQ (non-null)
     * @throws NullPointerException if any dependency is null
     */
    public AccountServiceImpl(AccountRepository repository,
                              AccountMapper mapper,
                              RabbitMqService mqService) {
        this.repository = Objects.requireNonNull(repository, "AccountRepository cannot be null");
        this.mapper = Objects.requireNonNull(mapper, "AccountMapper cannot be null");
        this.mqService = Objects.requireNonNull(mqService, "RabbitMqService cannot be null");
    }

    /**
     * {@inheritDoc}
     *
     * <p>If an account already exists for the document number, it is returned immediately
     * without creating a duplicate. Otherwise a new account is persisted and an event
     * is published to the {@code ACCOUNT} queue.</p>
     */
    @Override
    public AccountResponse createAccount(AccountRequest request) {
        Objects.requireNonNull(request, "AccountRequest cannot be null");
        log.info("Creating account. documentNumber={}", request.documentNumber());

        AccountDocument existing = repository.findByDocumentNumber(request.documentNumber());
        if (existing != null) {
            log.info("Account already exists. accountId={}", existing.getAccountId());
            return mapper.convertDocumentToResponse(existing);
        }

        AccountDocument created = repository.save(mapper.convertRequestToDocument(request));
        log.info("New account created. accountId={}", created.getAccountId());

        mqService.send(RabbitMqConstants.ACCOUNT,
                "New AccountID Created: " + created.getAccountId());

        return mapper.convertDocumentToResponse(created);
    }

    /**
     * {@inheritDoc}
     *
     * @throws AccountNotFoundException if no account exists with the given ID
     */
    @Override
    public AccountDetailResponse findById(String accountId) {
        Objects.requireNonNull(accountId, "Account ID cannot be null");
        log.info("Finding account. accountId={}", accountId);

        return repository.findById(accountId)
                .map(mapper::convertDocumentToDetailResponse)
                .orElseThrow(() -> {
                    log.warn("Account not found. accountId={}", accountId);
                    return new AccountNotFoundException(accountId);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccountDocument findByDocumentNumber(String documentNumber) {
        Objects.requireNonNull(documentNumber, "Document number cannot be null");
        return repository.findByDocumentNumber(documentNumber);
    }
}
