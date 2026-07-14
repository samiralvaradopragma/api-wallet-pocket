package co.com.nequi.walletpocket;

import co.com.nequi.walletpocket.domain.wallet.values.WalletId;
import co.com.nequi.walletpocket.domain.transaction.Transaction;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import co.com.nequi.walletpocket.wallet.gateaway.EventBusGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.Map;

@Component
public class ReactiveEventBusAdapter implements EventBusGateway {

    private static final Logger log = LoggerFactory.getLogger(ReactiveEventBusAdapter.class);
    private final RabbitTemplate rabbitTemplate;
    private static final String EXCHANGE_NAME = "wallet.events";
    private static final String ROUTING_KEY = "transaction.high.value";

    public ReactiveEventBusAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @CircuitBreaker(name = "rabbitCircuitBreaker", fallbackMethod = "publishFallback")
    public Mono<Void> publishHighValueTransaction(WalletId walletId, Transaction transaction) {
        return Mono.fromRunnable(() -> {
            Map<String, Object> eventBody = Map.of(
                    "eventId", java.util.UUID.randomUUID().toString(),
                    "walletId", walletId.value().toString(),
                    "transactionId", transaction.id().value().toString(),
                    "amount", transaction.amount().value(),
                    "description", transaction.description().value()
            );

            log.info("Publishing high-value event to RabbitMQ Exchange: {}", EXCHANGE_NAME);
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, eventBody);
        }).then();
    }

    public Mono<Void> publishFallback(WalletId walletId, Transaction transaction, Throwable exception) {
        log.error("Circuit Breaker Active! Failed to deliver high-value event for wallet {}. Reason: {}. Diverting to local DLQ fallback logs.",
                walletId.value(), exception.getMessage());
        return Mono.empty();
    }
}
