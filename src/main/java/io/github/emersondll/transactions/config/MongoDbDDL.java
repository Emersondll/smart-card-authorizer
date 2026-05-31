package io.github.emersondll.transactions.config;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import io.github.emersondll.transactions.config.constants.MongoDbOperationTypeConstants;
import io.github.emersondll.transactions.document.OperationsTypeDocument;
import io.github.emersondll.transactions.repository.OperationsTypeRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration class responsible for seeding the {@code operationsType} MongoDB collection
 * at application startup.
 *
 * <p>The four predefined operation types are upserted on every startup via
 * {@link OperationsTypeRepository#saveAll(Iterable)}. MongoDB's {@code _id} equality
 * ensures existing records are overwritten rather than duplicated.</p>
 *
 * <p>Predefined types:
 * <ol>
 *   <li>ID {@code 1}: COMPRA A VISTA (cash purchase)</li>
 *   <li>ID {@code 2}: COMPRA PARCELADA (instalment purchase)</li>
 *   <li>ID {@code 3}: SAQUE (withdrawal)</li>
 *   <li>ID {@code 4}: PAGAMENTO (payment)</li>
 * </ol>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see OperationsTypeRepository
 */
@Configuration
@Slf4j
public class MongoDbDDL {

    private final OperationsTypeRepository operationsTypeRepository;

    /**
     * Constructor-based dependency injection.
     *
     * @param operationsTypeRepository repository for seeding operation types (non-null)
     * @throws NullPointerException if {@code operationsTypeRepository} is null
     */
    public MongoDbDDL(OperationsTypeRepository operationsTypeRepository) {
        this.operationsTypeRepository = Objects.requireNonNull(
                operationsTypeRepository, "OperationsTypeRepository cannot be null");
    }

    /**
     * Seeds the {@code operationsType} collection with the four predefined operation types.
     * Executed once after the Spring context is fully initialised.
     */
    @PostConstruct
    private void init() {
        log.info("Seeding operation types into MongoDB");
        operationsTypeRepository.saveAll(buildOperationTypes());
        log.info("Operation types seeded successfully");
    }

    /**
     * Builds the list of predefined operation type documents.
     *
     * @return an immutable list of four {@link OperationsTypeDocument} instances
     */
    private List<OperationsTypeDocument> buildOperationTypes() {
        LocalDateTime now = LocalDateTime.now();
        return List.of(
                buildType("1", MongoDbOperationTypeConstants.BUY_AT_CASH, now),
                buildType("2", MongoDbOperationTypeConstants.INSTALLMENT_PURCHASE, now),
                buildType("3", MongoDbOperationTypeConstants.WITHDRAWAL, now),
                buildType("4", MongoDbOperationTypeConstants.PAYMENT, now)
        );
    }

    /**
     * Creates a single {@link OperationsTypeDocument} with the given parameters.
     *
     * @param id          the operation type identifier
     * @param description the human-readable description
     * @param createdAt   the seed timestamp
     * @return a new {@link OperationsTypeDocument}
     */
    private OperationsTypeDocument buildType(String id, String description, LocalDateTime createdAt) {
        return OperationsTypeDocument.builder()
                .operationsId(id)
                .description(description)
                .createdAt(createdAt)
                .build();
    }
}
