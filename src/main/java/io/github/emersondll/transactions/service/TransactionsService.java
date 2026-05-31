package io.github.emersondll.transactions.service;

import io.github.emersondll.transactions.exception.AccountNotFoundException;
import io.github.emersondll.transactions.exception.InvalidTransactionException;
import io.github.emersondll.transactions.exception.OperationTypeNotFoundException;
import io.github.emersondll.transactions.model.request.TransactionsRequest;
import io.github.emersondll.transactions.model.response.BalanceResponse;
import io.github.emersondll.transactions.model.response.TransactionsResponse;

/**
 * Service contract for financial transaction operations.
 *
 * <p>Defines the business interface for registering new transactions and
 * computing account balances. Implementations validate the account and
 * operation type before persisting, and publish events via RabbitMQ.</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see io.github.emersondll.transactions.service.impl.TransactionsServiceImpl
 */
public interface TransactionsService {

    /**
     * Registers a new financial transaction after validating the account and
     * operation type, normalising the amount sign, and publishing a domain event.
     *
     * @param request the transaction request containing account ID, operation type,
     *                and amount (non-null)
     * @return a {@link TransactionsResponse} with the persisted transaction data
     * @throws InvalidTransactionException     if required fields are missing or invalid
     * @throws AccountNotFoundException        if the referenced account does not exist
     * @throws OperationTypeNotFoundException  if the referenced operation type does not exist
     */
    TransactionsResponse createTransaction(TransactionsRequest request);

    /**
     * Computes the net balance for the account associated with the given document number.
     *
     * <p>The balance is the algebraic sum of all transaction amounts for that account.
     * Negative values indicate net debit; positive values indicate net credit.</p>
     *
     * @param documentNumber the customer's document number used to locate the account
     * @return a {@link BalanceResponse} with the computed net balance
     * @throws AccountNotFoundException if no account is registered for the document number
     */
    BalanceResponse recoveryBalance(String documentNumber);
}
