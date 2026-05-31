package io.github.emersondll.transactions.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.emersondll.transactions.document.AccountDocument;
import io.github.emersondll.transactions.exception.AccountNotFoundException;
import io.github.emersondll.transactions.mapper.AccountMapper;
import io.github.emersondll.transactions.model.request.AccountRequest;
import io.github.emersondll.transactions.model.response.AccountDetailResponse;
import io.github.emersondll.transactions.model.response.AccountResponse;
import io.github.emersondll.transactions.repository.AccountRepository;
import io.github.emersondll.transactions.service.AccountService;
import io.github.emersondll.transactions.service.RabbitMqService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AccountServiceImpl}.
 *
 * <p>All external dependencies are mocked with Mockito.
 * Tests cover the happy path, idempotent creation, and not-found scenarios.</p>
 */
@DisplayName("AccountServiceImpl Unit Tests")
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private AccountService testClass;

    @Mock
    private AccountRepository repository;

    @Mock
    private AccountMapper mapper;

    @Mock
    private RabbitMqService mqService;

    @BeforeEach
    void setup() {
        testClass = new AccountServiceImpl(repository, mapper, mqService);
    }

    @Test
    @DisplayName("createAccount should return existing account when document number is already registered")
    void shouldReturnExistingAccountWhenDocumentAlreadyRegistered() {
        Mockito.when(repository.findByDocumentNumber(Mockito.anyString()))
                .thenReturn(accountDocument());
        Mockito.when(mapper.convertDocumentToResponse(Mockito.any(AccountDocument.class)))
                .thenReturn(accountResponse());

        AccountResponse response = testClass.createAccount(accountRequest());

        Assertions.assertEquals("456", response.accountId());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("createAccount should create and return new account when document number is not registered")
    void shouldCreateNewAccountWhenDocumentNotRegistered() {
        Mockito.when(repository.findByDocumentNumber(Mockito.anyString())).thenReturn(null);
        Mockito.when(mapper.convertRequestToDocument(Mockito.any(AccountRequest.class)))
                .thenReturn(accountDocument());
        Mockito.when(repository.save(Mockito.any(AccountDocument.class)))
                .thenReturn(accountDocument());
        Mockito.when(mapper.convertDocumentToResponse(Mockito.any(AccountDocument.class)))
                .thenReturn(accountResponse());

        AccountResponse response = testClass.createAccount(accountRequest());

        Assertions.assertEquals("456", response.accountId());
        Mockito.verify(repository).save(Mockito.any());
        Mockito.verify(mqService).send(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    @DisplayName("findById should return account detail response when account exists")
    void shouldReturnAccountDetailWhenAccountExists() {
        Mockito.when(repository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(accountDocument()));
        Mockito.when(mapper.convertDocumentToDetailResponse(Mockito.any(AccountDocument.class)))
                .thenReturn(accountDetailResponse());

        AccountDetailResponse response = testClass.findById("accountId");

        Assertions.assertEquals("456", response.accountId());
        Assertions.assertEquals("123", response.documentNumber());
    }

    @Test
    @DisplayName("findById should throw AccountNotFoundException when account does not exist")
    void shouldThrowAccountNotFoundWhenAccountDoesNotExist() {
        Mockito.when(repository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> testClass.findById("nonExistentId")
        );

        assertTrue(exception.getMessage().contains("nonExistentId"));
    }

    // ---- helpers ----

    private AccountRequest accountRequest() {
        return new AccountRequest("963698");
    }

    private AccountDocument accountDocument() {
        return AccountDocument.builder()
                .accountId("456")
                .documentNumber("123")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private AccountResponse accountResponse() {
        return new AccountResponse("456");
    }

    private AccountDetailResponse accountDetailResponse() {
        return new AccountDetailResponse("456", "123");
    }
}
