package co.com.nequi.walletpocket.transaction;

import co.com.nequi.walletpocket.transaction.values.Description;
import co.com.nequi.walletpocket.transaction.values.TransactionId;
import co.com.nequi.walletpocket.wallet.values.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    @DisplayName("Debe instanciar correctamente una transacción con todos sus valores")
    void shouldCreateTransactionSuccessfully() {
        TransactionId id = TransactionId.generate();
        Description description = new Description("Abono Nómina");
        Money amount = new Money(new BigDecimal("500.00"));

        Transaction transaction = new Transaction(id, description, amount);

        assertEquals(id, transaction.id());
        assertEquals(description, transaction.description());
        assertEquals(new BigDecimal("500.00"), transaction.amount().value());
    }

    @Test
    @DisplayName("Debe actualizar el monto creando una nueva instancia inmutable y dejando intacta la original")
    void shouldUpdateAmountAndReturnNewInstance() {
        TransactionId id = TransactionId.generate();
        Description description = new Description("Retiro Corresponsal");
        Money originalAmount = new Money(new BigDecimal("40.00"));

        Transaction originalTx = new Transaction(id, description, originalAmount);

        Money newAmount = new Money(new BigDecimal("45.50"));
        Transaction updatedTx = originalTx.updateAmount(newAmount);

        assertNotSame(originalTx, updatedTx);
        assertEquals(new BigDecimal("40.00"), originalTx.amount().value());
        assertEquals(new BigDecimal("45.50"), updatedTx.amount().value());

        assertEquals(originalTx.id(), updatedTx.id());
        assertEquals(originalTx.description(), updatedTx.description());
    }
}