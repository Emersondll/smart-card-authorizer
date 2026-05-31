package io.github.emersondll.transactions.document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MongoDB document entity representing a financial transaction.
 *
 * <p>Records the details of a single debit, withdrawal, or payment operation
 * performed on an account. All transactions are stored in the
 * {@code transactions} collection and are immutable after creation.</p>
 *
 * <p>Amount sign convention:
 * <ul>
 *   <li>Negative ({@code < 0}): debit operations (purchases, withdrawals)</li>
 *   <li>Positive ({@code > 0}): credit operations (payments)</li>
 * </ul>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see io.github.emersondll.transactions.repository.TransactionsRepository
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class TransactionsDocument {

    /**
     * Unique transaction identifier — a UUID generated at registration time.
     * Acts as the MongoDB {@code _id} field.
     */
    @Id
    private String transactionsId;

    /**
     * Reference to the account that originated this transaction.
     */
    private String accountId;

    /**
     * Operation type identifier linking to the {@code operationsType} collection.
     * Values: {@code 1} (cash purchase), {@code 2} (instalment), {@code 3} (withdrawal),
     * {@code 4} (payment).
     */
    private String operationTypeId;

    /**
     * Net monetary value of the transaction after sign normalisation.
     * Negative for debits; positive for payments.
     */
    private BigDecimal amount;

    /**
     * UTC timestamp recording when the transaction was registered in the system.
     */
    private LocalDateTime eventDate;
}
