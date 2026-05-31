package io.github.emersondll.transactions.controller;

import java.net.URI;
import java.util.Objects;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.emersondll.transactions.model.request.TransactionsRequest;
import io.github.emersondll.transactions.model.response.BalanceResponse;
import io.github.emersondll.transactions.model.response.TransactionsResponse;
import io.github.emersondll.transactions.service.TransactionsService;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for transaction-related endpoints.
 *
 * <p>Handles HTTP request validation and response mapping.
 * All business logic is delegated to {@link TransactionsService}.</p>
 *
 * <p>Base path: {@code /digital/transactions/v1} (inherited from {@link BaseController})</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see TransactionsService for business logic
 */
@RestController
@Slf4j
public class TransactionsController implements BaseController {

    private final TransactionsService service;

    /**
     * Constructor-based dependency injection.
     *
     * @param service the transaction business logic service (non-null)
     * @throws NullPointerException if {@code service} is null
     */
    public TransactionsController(TransactionsService service) {
        this.service = Objects.requireNonNull(service, "TransactionsService cannot be null");
    }

    /**
     * Registers a new financial transaction.
     *
     * <p>HTTP Semantics:
     * <ul>
     *   <li>Method: {@code POST}</li>
     *   <li>URL: {@code /digital/transactions/v1/transactions}</li>
     *   <li>Success: {@code 201 CREATED} with {@code Location} header</li>
     *   <li>Invalid request: {@code 400 BAD REQUEST}</li>
     *   <li>Account not found: {@code 404 NOT FOUND}</li>
     * </ul>
     *
     * @param request the transaction request containing accountId, operationTypeId,
     *                and amount (non-null, validated via {@code @Valid})
     * @return {@link ResponseEntity} with {@code 201 CREATED} status and transaction data
     */
    @PostMapping("/transactions")
    public ResponseEntity<TransactionsResponse> createTransaction(
            @Valid @RequestBody TransactionsRequest request) {
        log.info("POST /transactions - accountId={}, operationTypeId={}",
                request.accountId(), request.operationTypeId());

        TransactionsResponse response = service.createTransaction(request);

        return ResponseEntity
                .created(URI.create("/digital/transactions/v1/transactions/" + response.transactionsId()))
                .body(response);
    }

    /**
     * Retrieves the net balance for the account associated with the given document number.
     *
     * <p>HTTP Semantics:
     * <ul>
     *   <li>Method: {@code GET}</li>
     *   <li>URL: {@code /digital/transactions/v1/transactions/balance/{documentNumber}}</li>
     *   <li>Success: {@code 200 OK} with net balance</li>
     *   <li>Not found: {@code 404 NOT FOUND}</li>
     * </ul>
     *
     * @param documentNumber the customer's document number (path variable)
     * @return {@link ResponseEntity} with {@code 200 OK} and the computed balance
     */
    @GetMapping("/transactions/balance/{documentNumber}")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable String documentNumber) {
        log.info("GET /transactions/balance/{}", documentNumber);

        BalanceResponse response = service.recoveryBalance(documentNumber);

        return ResponseEntity.ok(response);
    }
}
