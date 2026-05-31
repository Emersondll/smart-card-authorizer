package io.github.emersondll.transactions.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.emersondll.transactions.document.TransactionsDocument;

/**
 * MongoDB repository for {@link TransactionsDocument} persistence operations.
 *
 * <p>Combines Spring Data's standard CRUD via {@link MongoRepository} with
 * custom query methods from {@link TransactionsRepositoryCustom}. Spring Data
 * MongoDB automatically generates the implementation at runtime.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
@Repository
public interface TransactionsRepository extends MongoRepository<TransactionsDocument, String>,
        TransactionsRepositoryCustom {

    /**
     * Finds all transactions associated with the given account ID.
     *
     * <p>Spring Data MongoDB derives the query from the method name.</p>
     *
     * @param accountId the account identifier to filter by (non-null)
     * @return a list of matching {@link TransactionsDocument}; never {@code null}
     */
    @Query
    List<TransactionsDocument> findAllByAccountId(String accountId);
}
