package io.github.emersondll.transactions.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Immutable record representing a request to register a new financial transaction.
 *
 * <p>Records are fully immutable — the {@code validateSignalValues} method in the
 * service layer creates a new {@code TransactionsRequest} with the corrected amount
 * instead of mutating this instance.</p>
 *
 * @param accountId       the account identifier that owns this transaction (non-null, non-blank)
 * @param operationTypeId the operation type identifier (1=cash purchase, 2=installment,
 *                        3=withdrawal, 4=payment) (non-null, non-blank)
 * @param amount          the monetary value of the transaction (non-null)
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public record TransactionsRequest(

        @NotBlank(message = "Account ID is required")
        String accountId,

        @NotBlank(message = "Operation type ID is required")
        String operationTypeId,

        @NotNull(message = "Amount is required")
        BigDecimal amount

) {
    /**
     * Compact constructor — enforces null-safety on all fields.
     *
     * @throws NullPointerException if any required field is null
     */
    public TransactionsRequest {
        Objects.requireNonNull(accountId, "Account ID cannot be null");
        Objects.requireNonNull(operationTypeId, "Operation type ID cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");
    }

    /**
     * Returns a new {@code TransactionsRequest} with the given corrected amount,
     * preserving all other fields unchanged.
     *
     * <p>Used by the service layer to normalise the sign of the amount according
     * to the operation type without mutating this record.</p>
     *
     * @param correctedAmount the normalised monetary amount
     * @return a new immutable {@code TransactionsRequest}
     */
    public TransactionsRequest withAmount(BigDecimal correctedAmount) {
        return new TransactionsRequest(accountId, operationTypeId, correctedAmount);
    }
}
