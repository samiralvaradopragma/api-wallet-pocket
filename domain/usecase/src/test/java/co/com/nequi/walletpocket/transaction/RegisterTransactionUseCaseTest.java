package co.com.nequi.walletpocket.transaction;

import co.com.nequi.walletpocket.pocket.Pocket;
import co.com.nequi.walletpocket.pocket.values.PocketId;
import co.com.nequi.walletpocket.transaction.values.Description;
import co.com.nequi.walletpocket.wallet.Wallet;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterTransactionUseCaseTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private RegisterTransactionUseCase registerTransactionUseCase;

    @Test
    @DisplayName("Debe registrar y consolidar una nueva transacción en un bolsillo")
    void shouldRegisterTransactionSuccessfully() {
        WalletId walletId = WalletId.generate();
        PocketId pocketId = PocketId.generate();

        Pocket pocket = new Pocket(pocketId, "Comida", Money.zero(), List.of());
        Wallet wallet = new Wallet(walletId, "user1", List.of(pocket));

        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(Mono.just(wallet));

        Mono<Wallet> result = registerTransactionUseCase.execute(walletId, pocketId, new Description("Almuerzo"), new Money(new BigDecimal("15.00")));

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }
}
