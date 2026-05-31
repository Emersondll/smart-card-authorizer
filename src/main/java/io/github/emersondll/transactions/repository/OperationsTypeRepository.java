package io.github.emersondll.transactions.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.github.emersondll.transactions.document.OperationsTypeDocument;

/**
 * MongoDB repository for {@link OperationsTypeDocument} persistence operations.
 *
 * <p>Spring Data MongoDB automatically generates the implementation at runtime.
 * Operation types are pre-seeded at startup and are effectively read-only
 * during normal application operation.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 * @see io.github.emersondll.transactions.config.MongoDbDDL for seed data
 */
@Repository
public interface OperationsTypeRepository extends MongoRepository<OperationsTypeDocument, String> {
}
