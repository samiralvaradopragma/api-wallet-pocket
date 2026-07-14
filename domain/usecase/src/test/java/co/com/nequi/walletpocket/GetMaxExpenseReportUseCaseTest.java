package co.com.nequi.walletpocket;

import co.com.nequi.walletpocket.pocket.Pocket;
import co.com.nequi.walletpocket.pocket.values.PocketId;
import co.com.nequi.walletpocket.transaction.Transaction;
import co.com.nequi.walletpocket.transaction.values.Description;
import co.com.nequi.walletpocket.transaction.values.TransactionId;
import co.com.nequi.walletpocket.wallet.Wallet;
import co.com.nequi.walletpocket.wallet.exception.WalletNotFoundException;
import co.com.nequi.walletpocket.wallet.gateaway.WalletRepository;
import co.com.nequi.walletpocket.wallet.values.Money;
import co.com.nequi.walletpocket.wallet.values.WalletId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMaxExpenseReportUseCaseTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private GetMaxExpenseReportUseCase getMaxExpenseReportUseCase;

    @Test
    @DisplayName("Debe generar el reporte correctamente con la transacción máxima de cada bolsillo")
    void shouldGenerateMaxExpenseReportSuccessfully() {
        WalletId walletId = WalletId.generate();
        Transaction tx1 = new Transaction(TransactionId.generate(), new Description("Gasto A"), new Money(new BigDecimal("50.00")));
        Transaction tx2 = new Transaction(TransactionId.generate(), new Description("Gasto B"), new Money(new BigDecimal("120.00"))); // Max

        Pocket pocket = new Pocket(PocketId.generate(), "Viajes", new Money(new BigDecimal("200.00")), List.of(tx1, tx2));
        Wallet wallet = new Wallet(walletId, "user123", List.of(pocket));

        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));

        Mono<Map<String, Transaction>> result = getMaxExpenseReportUseCase.execute(walletId);

        StepVerifier.create(result)
                .expectNextMatches(report -> report.containsKey("Viajes") && report.get("Viajes").amount().value().compareTo(new BigDecimal("120.00")) == 0)
                .verifyComplete();
    }

    @Test
    @DisplayName("Edge Case: Debe manejar de forma segura bolsillos sin transacciones (listas vacías) sin lanzar NullPointerException")
    void shouldHandleEmptyTransactionListGracefully() {
        WalletId walletId = WalletId.generate();
        // Bolsillo con lista de transacciones vacía
        Pocket emptyPocket = new Pocket(PocketId.generate(), "Vacío", Money.zero(), Collections.emptyList());
        Wallet wallet = new Wallet(walletId, "user123", List.of(emptyPocket));

        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));

        Mono<Map<String, Transaction>> result = getMaxExpenseReportUseCase.execute(walletId);

        StepVerifier.create(result)
                .expectNextMatches(Map::isEmpty) // El mapa final debe quedar vacío para este bolsillo de forma segura
                .verifyComplete();
    }

    @Test
    @DisplayName("Edge Case: Debe lanzar WalletNotFoundException si la billetera no existe")
    void shouldFailWhenWalletDoesNotExist() {
        WalletId walletId = WalletId.generate();
        when(walletRepository.findById(walletId)).thenReturn(Mono.empty());

        Mono<Map<String, Transaction>> result = getMaxExpenseReportUseCase.execute(walletId);

        StepVerifier.create(result)
                .expectError(WalletNotFoundException.class)
                .verify();
    }
}
