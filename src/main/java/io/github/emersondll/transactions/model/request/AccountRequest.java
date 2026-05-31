package io.github.emersondll.transactions.model.request;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

/**
 * Immutable record representing a request to create a new account.
 *
 * <p>All fields are validated via Bean Validation annotations applied
 * before the compact constructor runs.</p>
 *
 * @param documentNumber the customer's unique document number (CPF/CNPJ).
 *                       Must be non-null and non-blank.
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public record AccountRequest(

        @NotBlank(message = "Document number is required")
        String documentNumber

) {
    /**
     * Compact constructor — enforces null-safety and trims the document number.
     *
     * @throws NullPointerException     if documentNumber is null
     * @throws IllegalArgumentException if documentNumber is blank after trimming
     */
    public AccountRequest {
        Objects.requireNonNull(documentNumber, "Document number cannot be null");
        documentNumber = documentNumber.strip();
        if (documentNumber.isEmpty()) {
            throw new IllegalArgumentException("Document number cannot be blank");
        }
    }
}
