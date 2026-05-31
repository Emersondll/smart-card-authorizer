package io.github.emersondll.transactions.service.impl;

import java.util.Objects;

import org.springframework.stereotype.Service;

import io.github.emersondll.transactions.document.OperationsTypeDocument;
import io.github.emersondll.transactions.exception.OperationTypeNotFoundException;
import io.github.emersondll.transactions.repository.OperationsTypeRepository;
import io.github.emersondll.transactions.service.OperationsTypeService;
import lombok.extern.slf4j.Slf4j;

/**
 * Business logic implementation for operation type look-up.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Retrieving pre-seeded operation type entities from MongoDB.</li>
 *   <li>Throwing a domain exception when a type is not found.</li>
 * </ul>
 *
 * <p>Thread Safety: Stateless service; thread-safe as a Spring singleton.</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see OperationsTypeRepository for persistence operations
 */
@Service
@Slf4j
public class OperationsTypeServiceImpl implements OperationsTypeService {

    private final OperationsTypeRepository repository;

    /**
     * Constructor-based dependency injection.
     *
     * @param repository repository for operation type persistence operations (non-null)
     * @throws NullPointerException if {@code repository} is null
     */
    public OperationsTypeServiceImpl(OperationsTypeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "OperationsTypeRepository cannot be null");
    }

    /**
     * {@inheritDoc}
     *
     * @throws OperationTypeNotFoundException if no operation type exists for the given ID
     */
    @Override
    public OperationsTypeDocument findById(String id) {
        Objects.requireNonNull(id, "Operation type ID cannot be null");
        log.info("Finding operation type. id={}", id);

        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Operation type not found. id={}", id);
                    return new OperationTypeNotFoundException(id);
                });
    }
}
