package co.com.nequi.walletpocket.wallet;

import co.com.nequi.walletpocket.pocket.Pocket;
import co.com.nequi.walletpocket.pocket.values.PocketId;
import co.com.nequi.walletpocket.wallet.values.Money;
import co.com.nequi.walletpocket.wallet.values.WalletId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    @DisplayName("Debe inicializar una Wallet con una lista inmutable de Pockets")
    void shouldInitializeWalletWithUnmodifiablePocketsList() {
        WalletId walletId = WalletId.generate();
        List<Pocket> pockets = new ArrayList<>();
        pockets.add(new Pocket(PocketId.generate(), "Comida", new Money(new BigDecimal("20.00")), List.of()));

        Wallet wallet = new Wallet(walletId, "samir123", pockets);

        assertEquals(1, wallet.pockets().size());

        assertThrows(UnsupportedOperationException.class, () ->
                wallet.pockets().add(new Pocket(PocketId.generate(), "Intruso", Money.zero(), List.of()))
        );
    }

    @Test
    @DisplayName("Debe agregar un nuevo Pocket a la Wallet de forma segura conservando la inmutabilidad")
    void shouldAddPocketSuccessfully() {
        Wallet wallet = new Wallet(WalletId.generate(), "samir123", List.of());
        Pocket newPocket = new Pocket(PocketId.generate(), "Servicios", new Money(new BigDecimal("50.00")), List.of());

        Wallet updatedWallet = wallet.addPocket(newPocket);

        assertEquals(0, wallet.pockets().size());
        assertEquals(1, updatedWallet.pockets().size());
        assertEquals("Servicios", updatedWallet.pockets().get(0).name());
    }

    @Test
    @DisplayName("Debe actualizar un Pocket existente dentro de la Wallet mapeando su ID")
    void shouldUpdateExistingPocketSuccessfully() {
        PocketId targetPocketId = PocketId.generate();
        Pocket originalPocket = new Pocket(targetPocketId, "Salud", new Money(new BigDecimal("100.00")), List.of());
        Wallet wallet = new Wallet(WalletId.generate(), "samir123", List.of(originalPocket));

        Pocket updatedPocket = new Pocket(targetPocketId, "Salud Premium", new Money(new BigDecimal("150.00")), List.of());

        Wallet walletResult = wallet.updatePocket(updatedPocket);

        assertEquals(1, walletResult.pockets().size());
        Pocket foundPocket = walletResult.pockets().get(0);
        assertEquals("Salud Premium", foundPocket.name());
        assertEquals(new BigDecimal("150.00"), foundPocket.balance().value());
    }

    @Test
    @DisplayName("Edge Case: La actualización de un Pocket no existente debe retornar la lista idéntica sin lanzar excepciones")
    void shouldReturnSamePocketsIfIdToUpdateDoesNotExist() {
        Pocket pocket = new Pocket(PocketId.generate(), "Entretenimiento", new Money(new BigDecimal("30.00")), List.of());
        Wallet wallet = new Wallet(WalletId.generate(), "samir123", List.of(pocket));

        Pocket ghostPocket = new Pocket(PocketId.generate(), "Fantasma", new Money(new BigDecimal("99.00")), List.of());

        Wallet walletResult = wallet.updatePocket(ghostPocket);

        assertEquals(1, walletResult.pockets().size());
        assertEquals("Entretenimiento", walletResult.pockets().get(0).name());
    }
}