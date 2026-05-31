package io.github.emersondll.transactions.repository;

import java.util.List;

import io.github.emersondll.transactions.document.TransactionsDocument;

/**
 * Custom extension interface for transaction-specific query operations.
 *
 * <p>Defines query methods that go beyond the standard Spring Data
 * CRUD operations. Implemented automatically by Spring Data MongoDB
 * when combined with {@link TransactionsRepository}.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public interface TransactionsRepositoryCustom {

    /**
     * Finds all transactions belonging to the given account.
     *
     * @param accountId the account identifier (non-null)
     * @return an ordered list of matching transactions; never {@code null},
     *         may be empty if no transactions exist
     */
    List<TransactionsDocument> findAllByAccountId(String accountId);
}
