package co.com.nequi.walletpocket;

import co.com.nequi.walletpocket.transaction.Transaction;
import co.com.nequi.walletpocket.transaction.values.Description;
import co.com.nequi.walletpocket.transaction.values.TransactionId;
import co.com.nequi.walletpocket.wallet.values.Money;
import co.com.nequi.walletpocket.wallet.values.WalletId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReactiveEventBusAdapterTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private ReactiveEventBusAdapter eventBusAdapter;

    @Captor
    private ArgumentCaptor<Map<String, Object>> eventBodyCaptor;

    private WalletId walletId;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        eventBusAdapter = new ReactiveEventBusAdapter(rabbitTemplate);
        walletId = WalletId.generate();
        transaction = new Transaction(
                TransactionId.generate(),
                new Description("Transferencia Alto Valor"),
                new Money(new BigDecimal("500000.00"))
        );
    }

    @Test
    @DisplayName("Debe empaquetar y publicar exitosamente el evento de alto valor en RabbitMQ")
    void shouldPublishHighValueTransactionSuccessfully() {

        Mono<Void> result = eventBusAdapter.publishHighValueTransaction(walletId, transaction);

        StepVerifier.create(result)
                .verifyComplete();

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("wallet.events"),
                eq("transaction.high.value"),
                eventBodyCaptor.capture()
        );

        Map<String, Object> capturedEvent = eventBodyCaptor.getValue();
        assertNotNull(capturedEvent);
        assertEquals(walletId.value().toString(), capturedEvent.get("walletId"));
        assertEquals(transaction.id().value().toString(), capturedEvent.get("transactionId"));
        assertEquals(new BigDecimal("500000.00"), capturedEvent.get("amount"));
        assertEquals("Transferencia Alto Valor", capturedEvent.get("description"));
        assertTrue(capturedEvent.containsKey("eventId"));
    }

    @Test
    @DisplayName("Edge Case / Fallback: El método fallback debe interceptar excepciones de forma segura y retornar un Mono vacío")
    void shouldExecutePublishFallbackOnCircuitBreakerTrigger() {

        RuntimeException simulatedException = new RuntimeException("Broker unreachable");

        Mono<Void> fallbackResult = eventBusAdapter.publishFallback(walletId, transaction, simulatedException);

        StepVerifier.create(fallbackResult)
                .verifyComplete();

        verifyNoInteractions(rabbitTemplate);
    }
}
