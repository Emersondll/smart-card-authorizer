package io.github.emersondll.transactions.mapper;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Component;

import io.github.emersondll.transactions.document.TransactionsDocument;
import io.github.emersondll.transactions.model.request.TransactionsRequest;
import io.github.emersondll.transactions.model.response.TransactionsResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Mapper component responsible for converting between {@link TransactionsRequest},
 * {@link TransactionsDocument}, and {@link TransactionsResponse}.
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
public class TransactionMapper {

    /**
     * Converts a {@link TransactionsRequest} into a new {@link TransactionsDocument}
     * ready to be persisted in MongoDB.
     *
     * <p>A random UUID is generated as the transaction identifier and
     * {@code eventDate} is set to the current UTC time. The request should
     * already have its amount sign normalised before calling this method.</p>
     *
     * @param request the incoming transaction request (non-null, amount already normalised)
     * @return a new {@link TransactionsDocument} with generated ID and timestamp
     * @throws NullPointerException if {@code request} is null
     */
    public TransactionsDocument requestToDocument(TransactionsRequest request) {
        Objects.requireNonNull(request, "TransactionsRequest cannot be null");
        log.debug("Converting TransactionsRequest to TransactionsDocument");

        return TransactionsDocument.builder()
                .transactionsId(UUID.randomUUID().toString())
                .accountId(request.accountId())
                .operationTypeId(request.operationTypeId())
                .amount(request.amount())
                .eventDate(LocalDateTime.now())
                .build();
    }

    /**
     * Converts a persisted {@link TransactionsDocument} into a
     * {@link TransactionsResponse} DTO suitable for the API response.
     *
     * @param document the persisted transaction document (non-null)
     * @return a {@link TransactionsResponse} with all transaction fields
     * @throws NullPointerException if {@code document} is null
     */
    public TransactionsResponse documentToResponse(TransactionsDocument document) {
        Objects.requireNonNull(document, "TransactionsDocument cannot be null");
        log.debug("Converting TransactionsDocument to TransactionsResponse");

        return new TransactionsResponse(
                document.getTransactionsId(),
                document.getAccountId(),
                document.getOperationTypeId(),
                document.getAmount(),
                document.getEventDate()
        );
    }
}
