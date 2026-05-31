package io.github.emersondll.transactions.service;

import io.github.emersondll.transactions.document.AccountDocument;
import io.github.emersondll.transactions.exception.AccountNotFoundException;
import io.github.emersondll.transactions.model.request.AccountRequest;
import io.github.emersondll.transactions.model.response.AccountDetailResponse;
import io.github.emersondll.transactions.model.response.AccountResponse;

/**
 * Service contract for account management operations.
 *
 * <p>Defines the business interface for creating and retrieving customer accounts.
 * Implementations must enforce idempotency on account creation (same document
 * number always returns the same account) and publish domain events via RabbitMQ.</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see io.github.emersondll.transactions.service.impl.AccountServiceImpl
 */
public interface AccountService {

    /**
     * Creates a new account for the given document number, or returns the existing
     * one if an account is already registered for that document number.
     *
     * <p>This operation is idempotent: calling it multiple times with the same
     * {@code documentNumber} always yields the same account.</p>
     *
     * @param request the account creation request containing the document number (non-null)
     * @return an {@link AccountResponse} with the account ID
     * @throws NullPointerException if {@code request} is null
     */
    AccountResponse createAccount(AccountRequest request);

    /**
     * Retrieves the full account details by the account's unique identifier.
     *
     * @param accountId the UUID of the account to retrieve (non-null, non-blank)
     * @return an {@link AccountDetailResponse} with account ID and document number
     * @throws AccountNotFoundException if no account exists with the given ID
     */
    AccountDetailResponse findById(String accountId);

    /**
     * Retrieves the raw {@link AccountDocument} entity for the given document number.
     *
     * <p>Intended for internal use by other services (e.g., balance calculation).
     * Returns {@code null} if no account is found — callers must handle this case.</p>
     *
     * @param documentNumber the customer's document number (non-null)
     * @return the matching {@link AccountDocument}, or {@code null} if not found
     */
    AccountDocument findByDocumentNumber(String documentNumber);
}
