package co.com.nequi.walletpocket.pocket.values;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PocketIdTest {

    @Test
    @DisplayName("Debe generar un PocketId válido de manera aleatoria")
    void shouldGenerateRandomPocketId() {
        PocketId pocketId = PocketId.generate();
        assertNotNull(pocketId);
        assertNotNull(pocketId.value());
    }

    @Test
    @DisplayName("Debe instanciar correctamente un PocketId a partir de una cadena UUID válida")
    void shouldCreatePocketIdFromString() {
        String uuidStr = UUID.randomUUID().toString();
        PocketId pocketId = PocketId.fromString(uuidStr);
        assertEquals(uuidStr, pocketId.value().toString());
    }

    @Test
    @DisplayName("Edge Case: Debe lanzar IllegalArgumentException si el UUID interno es nulo")
    void shouldThrowExceptionWhenValueIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new PocketId(null));
        assertEquals("PocketId cannot be null", exception.getMessage());
    }
}