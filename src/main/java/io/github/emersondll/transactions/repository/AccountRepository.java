package io.github.emersondll.transactions.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.emersondll.transactions.document.AccountDocument;

/**
 * MongoDB repository for {@link AccountDocument} persistence operations.
 *
 * <p>Combines Spring Data's standard CRUD via {@link MongoRepository} with
 * custom query methods from {@link AccountRepositoryCustom}. Spring Data
 * MongoDB automatically generates the implementation at runtime.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
@Repository
public interface AccountRepository extends MongoRepository<AccountDocument, String>,
        AccountRepositoryCustom {

    /**
     * Finds the account associated with the given document number.
     *
     * <p>Spring Data MongoDB derives the query from the method name.
     * Returns {@code null} if no account is found.</p>
     *
     * @param documentNumber the customer's document number (non-null)
     * @return the matching {@link AccountDocument}, or {@code null}
     */
    @Query
    AccountDocument findByDocumentNumber(String documentNumber);
}
