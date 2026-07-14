package co.com.nequi.walletpocket.transaction.values;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionIdTest {

    @Test
    @DisplayName("Debe generar un TransactionId aleatorio de forma exitosa")
    void shouldGenerateRandomTransactionId() {
        TransactionId txId = TransactionId.generate();
        assertNotNull(txId);
        assertNotNull(txId.value());
    }

    @Test
    @DisplayName("Debe instanciar correctamente un TransactionId a partir de una cadena UUID válida")
    void shouldCreateTransactionIdFromString() {
        String uuidStr = UUID.randomUUID().toString();
        TransactionId txId = TransactionId.fromString(uuidStr);
        assertEquals(uuidStr, txId.value().toString());
    }

    @Test
    @DisplayName("Edge Case: Debe lanzar IllegalArgumentException si el UUID interno es nulo")
    void shouldThrowExceptionWhenUuidIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new TransactionId(null));
        assertEquals("TransactionId cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Edge Case: Debe propagar IllegalArgumentException si la cadena para construir el UUID es inválida")
    void shouldThrowExceptionWhenStringIsInvalidUuid() {
        assertThrows(IllegalArgumentException.class, () -> TransactionId.fromString("id-invalido-123"));
    }
}
