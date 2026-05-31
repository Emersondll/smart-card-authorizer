package io.github.emersondll.transactions.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable record representing the result of a registered financial transaction.
 *
 * <p>Returned by {@code POST /transactions}. Contains the persisted transaction
 * details including the system-generated ID and event timestamp.</p>
 *
 * @param transactionsId  the system-generated unique transaction identifier (UUID)
 * @param accountId       the account identifier associated with this transaction
 * @param operationTypeId the operation type identifier (1–4)
 * @param amount          the monetary value after sign normalisation (negative = debit,
 *                        positive = payment/credit)
 * @param eventDate       the UTC timestamp when the transaction was recorded
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public record TransactionsResponse(
        String transactionsId,
        String accountId,
        String operationTypeId,
        BigDecimal amount,
        LocalDateTime eventDate
) {
    /**
     * Compact constructor — ensures all fields are non-null.
     *
     * @throws NullPointerException if any field is null
     */
    public TransactionsResponse {
        Objects.requireNonNull(transactionsId, "Transaction ID cannot be null");
        Objects.requireNonNull(accountId, "Account ID cannot be null");
        Objects.requireNonNull(operationTypeId, "Operation type ID cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(eventDate, "Event date cannot be null");
    }
}
