package io.github.emersondll.transactions.service;

import io.github.emersondll.transactions.document.OperationsTypeDocument;
import io.github.emersondll.transactions.exception.OperationTypeNotFoundException;

/**
 * Service contract for operation type look-up operations.
 *
 * <p>Operation types are seeded at startup and are read-only during normal
 * operation. Implementations return the full entity so the transaction service
 * can inspect the description to determine routing and sign normalisation rules.</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see io.github.emersondll.transactions.service.impl.OperationsTypeServiceImpl
 */
public interface OperationsTypeService {

    /**
     * Retrieves the operation type entity by its identifier.
     *
     * @param id the operation type identifier (e.g., "1", "2", "3", "4") (non-null)
     * @return the matching {@link OperationsTypeDocument}
     * @throws OperationTypeNotFoundException if no operation type exists for the given ID
     */
    OperationsTypeDocument findById(String id);
}
