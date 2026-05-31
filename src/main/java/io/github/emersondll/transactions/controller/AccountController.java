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

import io.github.emersondll.transactions.model.request.AccountRequest;
import io.github.emersondll.transactions.model.response.AccountDetailResponse;
import io.github.emersondll.transactions.model.response.AccountResponse;
import io.github.emersondll.transactions.service.AccountService;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for account-related endpoints.
 *
 * <p>Handles HTTP request validation and response mapping.
 * All business logic is delegated to {@link AccountService}.</p>
 *
 * <p>Base path: {@code /digital/transactions/v1} (inherited from {@link BaseController})</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see AccountService for business logic
 */
@RestController
@Slf4j
public class AccountController implements BaseController {

    private final AccountService service;

    /**
     * Constructor-based dependency injection.
     *
     * @param service the account business logic service (non-null)
     * @throws NullPointerException if {@code service} is null
     */
    public AccountController(AccountService service) {
        this.service = Objects.requireNonNull(service, "AccountService cannot be null");
    }

    /**
     * Creates a new account or returns the existing one for the given document number.
     *
     * <p>HTTP Semantics:
     * <ul>
     *   <li>Method: {@code POST}</li>
     *   <li>URL: {@code /digital/transactions/v1/accounts}</li>
     *   <li>Success: {@code 201 CREATED} with {@code Location} header</li>
     * </ul>
     *
     * @param request the account creation request containing the document number (non-null)
     * @return {@link ResponseEntity} with {@code 201 CREATED} status and the account ID
     */
    @PostMapping("/accounts")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {
        log.info("POST /accounts - documentNumber={}", request.documentNumber());

        AccountResponse response = service.createAccount(request);

        return ResponseEntity
                .created(URI.create("/digital/transactions/v1/accounts/" + response.accountId()))
                .body(response);
    }

    /**
     * Retrieves the full account details by account ID.
     *
     * <p>HTTP Semantics:
     * <ul>
     *   <li>Method: {@code GET}</li>
     *   <li>URL: {@code /digital/transactions/v1/accounts/{accountId}}</li>
     *   <li>Success: {@code 200 OK} with account ID and document number</li>
     *   <li>Not found: {@code 404 NOT FOUND} (handled by global exception handler)</li>
     * </ul>
     *
     * @param accountId the UUID of the account to retrieve (path variable)
     * @return {@link ResponseEntity} with {@code 200 OK} and full account details
     */
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<AccountDetailResponse> getAccount(@PathVariable String accountId) {
        log.info("GET /accounts/{}", accountId);

        AccountDetailResponse response = service.findById(accountId);

        return ResponseEntity.ok(response);
    }
}
