package io.github.emersondll.transactions.mapper;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Component;

import io.github.emersondll.transactions.document.AccountDocument;
import io.github.emersondll.transactions.model.request.AccountRequest;
import io.github.emersondll.transactions.model.response.AccountDetailResponse;
import io.github.emersondll.transactions.model.response.AccountResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Mapper component responsible for converting between {@link AccountRequest},
 * {@link AccountDocument}, {@link AccountResponse}, and {@link AccountDetailResponse}.
 *
 * <p>All conversions are stateless and thread-safe. This component contains no
 * business logic — it only translates data between layers.</p>
 *
 * <p>Thread Safety: Stateless and thread-safe; safe to use as a Spring singleton.</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 */
@Component
@Slf4j
public class AccountMapper {

    /**
     * Converts an {@link AccountRequest} into a new {@link AccountDocument}
     * ready to be persisted in MongoDB.
     *
     * <p>A random UUID is generated as the account identifier and
     * {@code createdAt} is set to the current UTC time.</p>
     *
     * @param request the incoming account creation request (non-null)
     * @return a new {@link AccountDocument} with generated ID and timestamp
     * @throws NullPointerException if {@code request} is null
     */
    public AccountDocument convertRequestToDocument(AccountRequest request) {
        Objects.requireNonNull(request, "AccountRequest cannot be null");
        log.debug("Converting AccountRequest to AccountDocument");

        return AccountDocument.builder()
                .accountId(UUID.randomUUID().toString())
                .documentNumber(request.documentNumber())
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Converts an {@link AccountDocument} into a minimal {@link AccountResponse}
     * containing only the account identifier.
     *
     * <p>Used after account creation to return the generated ID.</p>
     *
     * @param document the persisted account document (non-null)
     * @return an {@link AccountResponse} with the account ID
     * @throws NullPointerException if {@code document} is null
     */
    public AccountResponse convertDocumentToResponse(AccountDocument document) {
        Objects.requireNonNull(document, "AccountDocument cannot be null");
        log.debug("Converting AccountDocument to AccountResponse");

        return new AccountResponse(document.getAccountId());
    }

    /**
     * Converts an {@link AccountDocument} into a full {@link AccountDetailResponse}
     * containing both the account ID and the customer's document number.
     *
     * <p>Used by the {@code GET /accounts/{accountId}} endpoint to return
     * the complete account view.</p>
     *
     * @param document the persisted account document (non-null)
     * @return an {@link AccountDetailResponse} with account ID and document number
     * @throws NullPointerException if {@code document} is null
     */
    public AccountDetailResponse convertDocumentToDetailResponse(AccountDocument document) {
        Objects.requireNonNull(document, "AccountDocument cannot be null");
        log.debug("Converting AccountDocument to AccountDetailResponse");

        return new AccountDetailResponse(document.getAccountId(), document.getDocumentNumber());
    }
}
