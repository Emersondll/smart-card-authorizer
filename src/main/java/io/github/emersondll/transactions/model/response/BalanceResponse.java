package io.github.emersondll.transactions.model.response;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Immutable record representing the computed balance for a given document number.
 *
 * <p>Returned by {@code GET /transactions/balance/{documentNumber}}.
 * The amount is the algebraic sum of all transaction values registered
 * for the account (debits are negative, payments are positive).</p>
 *
 * @param amount the net balance amount; never {@code null}
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public record BalanceResponse(BigDecimal amount) {

    /**
     * Compact constructor — ensures amount is never null.
     *
     * @throws NullPointerException if amount is null
     */
    public BalanceResponse {
        Objects.requireNonNull(amount, "Amount cannot be null");
    }
}
