package io.github.emersondll.transactions.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MongoDB document entity representing a customer account.
 *
 * <p>Each account is uniquely identified by a UUID ({@code accountId}) and
 * associated with a customer's document number (CPF/CNPJ). Accounts are
 * stored in the {@code account} collection.</p>
 *
 * <p>Thread Safety: Instances are mutable and not thread-safe. Never share
 * a document instance across threads without external synchronisation.</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see io.github.emersondll.transactions.repository.AccountRepository
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "account")
public class AccountDocument {

    /**
     * Unique account identifier — a UUID generated at creation time.
     * Acts as the MongoDB {@code _id} field.
     */
    @Id
    private String accountId;

    /**
     * The customer's document number (CPF or CNPJ) associated with this account.
     * Indexed for fast look-ups by document number.
     */
    private String documentNumber;

    /**
     * UTC timestamp recording when the account was created.
     * Immutable after initial persistence.
     */
    private LocalDateTime createdAt;
}
