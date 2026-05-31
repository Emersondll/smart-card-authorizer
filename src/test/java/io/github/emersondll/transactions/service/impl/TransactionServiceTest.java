package io.github.emersondll.transactions.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.emersondll.transactions.document.AccountDocument;
import io.github.emersondll.transactions.document.OperationsTypeDocument;
import io.github.emersondll.transactions.document.TransactionsDocument;
import io.github.emersondll.transactions.exception.InvalidTransactionException;
import io.github.emersondll.transactions.mapper.TransactionMapper;
import io.github.emersondll.transactions.model.request.TransactionsRequest;
import io.github.emersondll.transactions.model.response.AccountDetailResponse;
import io.github.emersondll.transactions.model.response.BalanceResponse;
import io.github.emersondll.transactions.model.response.TransactionsResponse;
import io.github.emersondll.transactions.repository.TransactionsRepository;
import io.github.emersondll.transactions.service.AccountService;
import io.github.emersondll.transactions.service.OperationsTypeService;
import io.github.emersondll.transactions.service.RabbitMqService;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link TransactionsServiceImpl}.
 *
 * <p>Covers amount sign normalisation, queue resolution, transaction creation,
 * invalid input handling, and balance computation.</p>
 */
@DisplayName("TransactionsServiceImpl Unit Tests")
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private TransactionsServiceImpl testClass;

    @Mock
    private AccountService accountService;

    @Mock
    private OperationsTypeService typeService;

    @Mock
    private TransactionsRepository repository;

    @Mock
    private TransactionMapper mapper;

    @Mock
    private RabbitMqService mqService;

    @BeforeEach
    void setup() {
        testClass = new TransactionsServiceImpl(accountService, typeService, repository, mapper, mqService);
    }

    // ---- resolveQueue ----

    @Test
    @DisplayName("resolveQueue should return PURCHASE for cash purchase operation")
    void shouldResolvePurchaseQueueForCashPurchase() {
        String queue = testClass.resolveQueue(operationType("COMPRA A VISTA"));
        Assertions.assertEquals("PURCHASE", queue);
    }

    @Test
    @DisplayName("resolveQueue should return PURCHASE for instalment purchase operation")
    void shouldResolvePurchaseQueueForInstalmentPurchase() {
        String queue = testClass.resolveQueue(operationType("COMPRA PARCELADA"));
        Assertions.assertEquals("PURCHASE", queue);
    }

    @Test
    @DisplayName("resolveQueue should return WITHDRAWAL for withdrawal operation")
    void shouldResolveWithdrawalQueueForWithdrawal() {
        String queue = testClass.resolveQueue(operationType("SAQUE"));
        Assertions.assertEquals("WITHDRAWAL", queue);
    }

    @Test
    @DisplayName("resolveQueue should return PAYMENT for payment operation")
    void shouldResolvePaymentQueueForPayment() {
        String queue = testClass.resolveQueue(operationType("PAGAMENTO"));
        Assertions.assertEquals("PAYMENT", queue);
    }

    // ---- normaliseAmountSign ----

    @Test
    @DisplayName("normaliseAmountSign should keep amount negative for cash purchase with positive input")
    void shouldNegatePositiveAmountForCashPurchase() {
        TransactionsRequest request = transactionRequest(new BigDecimal(10));
        TransactionsRequest result = testClass.normaliseAmountSign(request, operationType("COMPRA A VISTA"));
        Assertions.assertEquals(new BigDecimal(-10), result.amount());
    }

    @Test
    @DisplayName("normaliseAmountSign should keep amount negative for cash purchase with negative input")
    void shouldKeepNegativeAmountForCashPurchase() {
        TransactionsRequest request = transactionRequest(new BigDecimal(-10));
        TransactionsRequest result = testClass.normaliseAmountSign(request, operationType("COMPRA A VISTA"));
        Assertions.assertEquals(new BigDecimal(-10), result.amount());
    }

    @Test
    @DisplayName("normaliseAmountSign should keep amount negative for instalment purchase with positive input")
    void shouldNegatePositiveAmountForInstalmentPurchase() {
        TransactionsRequest request = transactionRequest(new BigDecimal(10));
        TransactionsRequest result = testClass.normaliseAmountSign(request, operationType("COMPRA PARCELADA"));
        Assertions.assertEquals(new BigDecimal(-10), result.amount());
    }

    @Test
    @DisplayName("normaliseAmountSign should keep amount negative for withdrawal with already negative input")
    void shouldKeepNegativeAmountForWithdrawal() {
        TransactionsRequest request = transactionRequest(new BigDecimal(-10));
        TransactionsRequest result = testClass.normaliseAmountSign(request, operationType("SAQUE"));
        Assertions.assertEquals(new BigDecimal(-10), result.amount());
    }

    @Test
    @DisplayName("normaliseAmountSign should make amount positive for payment with positive input")
    void shouldKeepPositiveAmountForPayment() {
        TransactionsRequest request = transactionRequest(new BigDecimal(10));
        TransactionsRequest result = testClass.normaliseAmountSign(request, operationType("PAGAMENTO"));
        Assertions.assertEquals(new BigDecimal(10), result.amount());
    }

    @Test
    @DisplayName("normaliseAmountSign should negate negative amount for payment")
    void shouldNegateNegativeAmountForPayment() {
        TransactionsRequest request = transactionRequest(new BigDecimal(-10));
        TransactionsRequest result = testClass.normaliseAmountSign(request, operationType("PAGAMENTO"));
        Assertions.assertEquals(new BigDecimal(10), result.amount());
    }

    // ---- createTransaction ----

    @Test
    @DisplayName("createTransaction should return response when all inputs are valid")
    void shouldCreateTransactionSuccessfully() throws Exception {
        Mockito.when(typeService.findById(Mockito.anyString())).thenReturn(operationType("COMPRA A VISTA"));
        Mockito.when(accountService.findById(Mockito.anyString())).thenReturn(accountDetailResponse());
        Mockito.when(mapper.requestToDocument(Mockito.any(TransactionsRequest.class)))
                .thenReturn(transactionsDocument());
        Mockito.when(repository.save(Mockito.any(TransactionsDocument.class)))
                .thenReturn(transactionsDocument());
        Mockito.when(mapper.documentToResponse(Mockito.any(TransactionsDocument.class)))
                .thenReturn(transactionResponse());

        TransactionsResponse response = testClass.createTransaction(transactionRequest(new BigDecimal(10)));

        Assertions.assertEquals(new BigDecimal(20), response.amount());
    }

    @Test
    @DisplayName("createTransaction should throw InvalidTransactionException when amount is null")
    void shouldThrowInvalidTransactionWhenAmountIsNull() {
        TransactionsRequest request = new TransactionsRequest("accountId", "1", BigDecimal.ONE)
                .withAmount(null);

        assertThrows(NullPointerException.class, () -> testClass.createTransaction(request));
    }

    // ---- recoveryBalance ----

    @Test
    @DisplayName("recoveryBalance should return sum of all transaction amounts")
    void shouldComputeBalanceAsTransactionSum() {
        Mockito.when(accountService.findByDocumentNumber(Mockito.anyString()))
                .thenReturn(accountDocument());
        Mockito.when(repository.findAllByAccountId(Mockito.anyString()))
                .thenReturn(List.of(transactionsDocument()));

        BalanceResponse response = testClass.recoveryBalance("anyDocumentNumber");

        Assertions.assertEquals(new BigDecimal(10), response.amount());
    }

    // ---- helpers ----

    private TransactionsResponse transactionResponse() {
        return new TransactionsResponse("5", "1", "6", new BigDecimal(20), LocalDateTime.now());
    }

    private TransactionsDocument transactionsDocument() {
        return TransactionsDocument.builder()
                .transactionsId("1")
                .amount(new BigDecimal(10))
                .build();
    }

    private TransactionsRequest transactionRequest(BigDecimal amount) {
        return new TransactionsRequest("accountId", "1", amount);
    }

    private OperationsTypeDocument operationType(String description) {
        return OperationsTypeDocument.builder()
                .operationsId("1")
                .description(description)
                .build();
    }

    private AccountDetailResponse accountDetailResponse() {
        return new AccountDetailResponse("456", "documentNumber");
    }

    private AccountDocument accountDocument() {
        return AccountDocument.builder()
                .accountId("accountId")
                .documentNumber("documentNumber")
                .build();
    }
}
