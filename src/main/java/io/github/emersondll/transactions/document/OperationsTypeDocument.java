package io.github.emersondll.transactions.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MongoDB document entity representing an operation type (transaction category).
 *
 * <p>Operation types are pre-seeded at application startup by
 * {@link io.github.emersondll.transactions.config.MongoDbDDL} and are
 * read-only during normal operation. They are stored in the
 * {@code operationsType} collection.</p>
 *
 * <p>Predefined types:
 * <ol>
 *   <li>{@code COMPRA A VISTA} — cash purchase (debit)</li>
 *   <li>{@code COMPRA PARCELADA} — instalment purchase (debit)</li>
 *   <li>{@code SAQUE} — withdrawal (debit)</li>
 *   <li>{@code PAGAMENTO} — payment (credit)</li>
 * </ol>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see io.github.emersondll.transactions.repository.OperationsTypeRepository
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "operationsType")
public class OperationsTypeDocument {

    /**
     * Unique operation type identifier (string "1"–"4").
     * Acts as the MongoDB {@code _id} field.
     */
    @Id
    private String operationsId;

    /**
     * Human-readable description of the operation type (e.g., "COMPRA A VISTA").
     * Used by the service layer to determine the RabbitMQ queue and amount sign.
     */
    private String description;

    /**
     * UTC timestamp recording when this operation type was seeded.
     */
    private LocalDateTime createdAt;
}
