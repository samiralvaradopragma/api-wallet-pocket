package co.com.nequi.walletpocket.pocket;

import co.com.nequi.walletpocket.pocket.values.PocketId;
import co.com.nequi.walletpocket.transaction.Transaction;
import co.com.nequi.walletpocket.transaction.values.Description;
import co.com.nequi.walletpocket.transaction.values.TransactionId;
import co.com.nequi.walletpocket.wallet.values.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PocketTest {

    @Test
    @DisplayName("Debe inicializar un Pocket inmutable con la lista de transacciones provista")
    void shouldInitializePocketWithUnmodifiableList() {
        PocketId pocketId = PocketId.generate();
        Money balance = new Money(new BigDecimal("100.00"));

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(TransactionId.generate(), new Description("Abono"), new Money(new BigDecimal("50.00"))));

        Pocket pocket = new Pocket(pocketId, "Ahorro", balance, transactions);

        assertNotNull(pocket.transactions());
        assertEquals(1, pocket.transactions().size());

        assertThrows(UnsupportedOperationException.class, () ->
                pocket.transactions().add(new Transaction(TransactionId.generate(), new Description("Fallo"), new Money(new BigDecimal("10.00"))))
        );
    }

    @Test
    @DisplayName("Debe agregar una transacción y recalcular el saldo del Pocket sumando de forma segura")
    void shouldAddTransactionAndCalculateNewBalance() {
        Pocket pocket = new Pocket(PocketId.generate(), "Viajes", new Money(new BigDecimal("100.00")), List.of());
        Transaction newTx = new Transaction(TransactionId.generate(), new Description("Depósito"), new Money(new BigDecimal("50.00")));

        Pocket updatedPocket = pocket.addTransaction(newTx);

        assertEquals(new BigDecimal("150.00"), updatedPocket.balance().value());
        assertEquals(1, updatedPocket.transactions().size());
        assertTrue(updatedPocket.transactions().contains(newTx));
    }

    @Test
    @DisplayName("Edge Case: Debe modificar una transacción y recalcular de forma segura el balance mediante reducción")
    void shouldModifyTransactionAndReevaluateBalanceSecurely() {
        TransactionId txIdToModify = TransactionId.generate();
        Transaction tx1 = new Transaction(txIdToModify, new Description("Compra A"), new Money(new BigDecimal("100.00")));
        Transaction tx2 = new Transaction(TransactionId.generate(), new Description("Compra B"), new Money(new BigDecimal("50.00")));

        Pocket pocket = new Pocket(PocketId.generate(), "General", new Money(new BigDecimal("150.00")), List.of(tx1, tx2));

        Transaction updatedTx1 = tx1.updateAmount(new Money(new BigDecimal("120.00")));

        Pocket updatedPocket = pocket.modifyTransaction(updatedTx1);

        assertEquals(new BigDecimal("170.00"), updatedPocket.balance().value());
        assertEquals(2, updatedPocket.transactions().size());

        Transaction foundTx = updatedPocket.transactions().stream()
                .filter(t -> t.id().equals(txIdToModify))
                .findFirst()
                .orElseThrow();
        assertEquals(new BigDecimal("120.00"), foundTx.amount().value());
    }

    @Test
    @DisplayName("Edge Case / Reduce Validation: El recálculo de balance de un Pocket modificado con lista vacía debe retornar balance CERO sin lanzar NullPointerException")
    void shouldPreventNullPointerAndReturnZeroBalanceOnEmptyListReevaluation() {
        Pocket pocket = new Pocket(PocketId.generate(), "Vacío", new Money(new BigDecimal("0.00")), List.of());

        Transaction externalTx = new Transaction(TransactionId.generate(), new Description("Ajeno"), new Money(new BigDecimal("80.00")));

        Pocket updatedPocket = pocket.modifyTransaction(externalTx);

        assertNotNull(updatedPocket.balance());
        assertEquals(BigDecimal.ZERO, updatedPocket.balance().value());
    }
}