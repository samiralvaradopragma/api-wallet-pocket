package co.com.nequi.walletpocket.transaction.values;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DescriptionTest {

    @Test
    @DisplayName("Debe crear una descripción si el valor proporcionado es válido")
    void shouldCreateDescriptionWhenValueIsValid() {
        Description description = new Description("Pago de servicios públicos");
        assertEquals("Pago de servicios públicos", description.value());
    }

    @Test
    @DisplayName("Edge Case: Debe lanzar IllegalArgumentException si la descripción es nula")
    void shouldThrowExceptionWhenDescriptionIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Description(null));
        assertEquals("Transaction description cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Edge Case: Debe lanzar IllegalArgumentException si la descripción viene vacía o con solo espacios")
    void shouldThrowExceptionWhenDescriptionIsEmptyOrBlank() {
        IllegalArgumentException exceptionEmpty = assertThrows(IllegalArgumentException.class, () -> new Description(""));
        IllegalArgumentException exceptionBlank = assertThrows(IllegalArgumentException.class, () -> new Description("   "));

        assertEquals("Transaction description cannot be empty", exceptionEmpty.getMessage());
        assertEquals("Transaction description cannot be empty", exceptionBlank.getMessage());
    }
}
