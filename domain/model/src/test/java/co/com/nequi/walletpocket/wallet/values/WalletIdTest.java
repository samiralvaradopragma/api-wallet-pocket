package co.com.nequi.walletpocket.wallet.values;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WalletIdTest {

    @Test
    @DisplayName("Debe generar un WalletId de forma aleatoria con éxito")
    void shouldGenerateRandomWalletId() {
        WalletId walletId = WalletId.generate();
        assertNotNull(walletId);
        assertNotNull(walletId.value());
    }

    @Test
    @DisplayName("Debe construir un WalletId a partir de una cadena UUID válida")
    void shouldCreateWalletIdFromString() {
        String uuidStr = UUID.randomUUID().toString();
        WalletId walletId = WalletId.fromString(uuidStr);
        assertEquals(uuidStr, walletId.value().toString());
    }

    @Test
    @DisplayName("Edge Case: Debe lanzar IllegalArgumentException si el UUID interno es nulo")
    void shouldThrowExceptionWhenUuidIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new WalletId(null));
        assertEquals("WalletId cannot be null", exception.getMessage());
    }
}