package io.github.emersondll.transactions.model.response;

import java.util.Objects;

/**
 * Immutable record representing the full account detail response.
 *
 * <p>Returned by the account retrieval endpoint ({@code GET /accounts/{accountId}}).
 * Extends the minimal view by also exposing the customer's document number.</p>
 *
 * @param accountId      the system-generated unique account identifier (UUID)
 * @param documentNumber the customer's document number associated with this account
 *
 * @author Emerson Lima
 * @since 1.0.0
 * @see AccountResponse for the minimal creation response
 */
public record AccountDetailResponse(String accountId, String documentNumber) {

    /**
     * Compact constructor — ensures neither field is null.
     *
     * @throws NullPointerException if accountId or documentNumber is null
     */
    public AccountDetailResponse {
        Objects.requireNonNull(accountId, "Account ID cannot be null");
        Objects.requireNonNull(documentNumber, "Document number cannot be null");
    }
}
