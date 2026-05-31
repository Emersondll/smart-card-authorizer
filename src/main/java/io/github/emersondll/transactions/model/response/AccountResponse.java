package io.github.emersondll.transactions.model.response;

import java.util.Objects;

/**
 * Immutable record representing the minimal account creation response.
 *
 * <p>Returned by the account creation endpoint ({@code POST /accounts}).
 * Contains only the generated account identifier so the caller can
 * reference the account in subsequent operations.</p>
 *
 * @param accountId the system-generated unique account identifier (UUID)
 *
 * @author Emerson Lima
 * @since 1.0.0
 * @see AccountDetailResponse for the full account view including document number
 */
public record AccountResponse(String accountId) {

    /**
     * Compact constructor — ensures accountId is never null.
     *
     * @throws NullPointerException if accountId is null
     */
    public AccountResponse {
        Objects.requireNonNull(accountId, "Account ID cannot be null");
    }
}
