package co.com.nequi.walletpocket.wallet.values;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    @DisplayName("Debe instanciar Money correctamente si el valor es positivo")
    void shouldCreateMoneyWhenValueIsValid() {
        Money money = new Money(new BigDecimal("150.50"));
        assertEquals(new BigDecimal("150.50"), money.value());
    }

    @Test
    @DisplayName("Debe permitir crear un objeto Money con valor Cero usando el método de fábrica")
    void shouldCreateZeroMoney() {
        Money zeroMoney = Money.zero();
        assertEquals(BigDecimal.ZERO, zeroMoney.value());
    }

    @Test
    @DisplayName("Edge Case: Debe lanzar NullPointerException si el valor provisto es nulo")
    void shouldThrowExceptionWhenValueIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> new Money(null));
        assertEquals("Money value cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Edge Case: Debe lanzar IllegalArgumentException si el valor es negativo")
    void shouldThrowExceptionWhenValueIsNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Money(new BigDecimal("-0.01")));
        assertEquals("Money value cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Debe sumar dos montos de Money de forma segura")
    void shouldAddMoneyCorrectly() {
        Money money1 = new Money(new BigDecimal("100.00"));
        Money money2 = new Money(new BigDecimal("50.00"));

        Money result = money1.add(money2);

        assertEquals(new BigDecimal("150.00"), result.value());
    }

    @Test
    @DisplayName("Debe restar dos montos de Money correctamente")
    void shouldSubtractMoneyCorrectly() {
        Money money1 = new Money(new BigDecimal("100.00"));
        Money money2 = new Money(new BigDecimal("30.00"));

        Money result = money1.subtract(money2);

        assertEquals(new BigDecimal("70.00"), result.value());
    }

    @Test
    @DisplayName("Edge Case: Debe lanzar excepción si el resultado de una resta da un valor negativo")
    void shouldThrowExceptionWhenSubtractionResultsInNegative() {
        Money money1 = new Money(new BigDecimal("50.00"));
        Money money2 = new Money(new BigDecimal("60.00"));

        assertThrows(IllegalArgumentException.class, () -> money1.subtract(money2));
    }

    @Test
    @DisplayName("Debe evaluar correctamente si un monto es mayor que otro")
    void shouldEvaluateIsGreaterThanCorrectly() {
        Money big = new Money(new BigDecimal("10.00"));
        Money small = new Money(new BigDecimal("5.00"));

        assertTrue(big.isGreaterThan(small));
        assertFalse(small.isGreaterThan(big));
    }
}
