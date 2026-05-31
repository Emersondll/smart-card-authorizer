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

import io.github.emersondll.transactions.document.OperationsTypeDocument;
import io.github.emersondll.transactions.exception.OperationTypeNotFoundException;
import io.github.emersondll.transactions.repository.OperationsTypeRepository;
import io.github.emersondll.transactions.service.OperationsTypeService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link OperationsTypeServiceImpl}.
 *
 * <p>Covers the happy path and not-found scenario for operation type look-up.</p>
 */
@DisplayName("OperationsTypeServiceImpl Unit Tests")
@ExtendWith(MockitoExtension.class)
class OperationsTypeServiceTest {

    private OperationsTypeService testClass;

    @Mock
    private OperationsTypeRepository repository;

    @BeforeEach
    void setup() {
        testClass = new OperationsTypeServiceImpl(repository);
    }

    @Test
    @DisplayName("findById should return operation type when it exists")
    void shouldReturnOperationTypeWhenItExists() throws Exception {
        Mockito.when(repository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(operationsTypeDocument()));

        OperationsTypeDocument response = testClass.findById("1");

        Assertions.assertEquals("456", response.getOperationsId());
    }

    @Test
    @DisplayName("findById should throw OperationTypeNotFoundException when it does not exist")
    void shouldThrowOperationTypeNotFoundWhenItDoesNotExist() {
        Mockito.when(repository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        OperationTypeNotFoundException exception = assertThrows(
                OperationTypeNotFoundException.class,
                () -> testClass.findById("nonExistentId")
        );

        assertTrue(exception.getMessage().contains("nonExistentId"));
    }

    // ---- helpers ----

    private OperationsTypeDocument operationsTypeDocument() {
        return OperationsTypeDocument.builder()
                .operationsId("456")
                .description("Description test")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
