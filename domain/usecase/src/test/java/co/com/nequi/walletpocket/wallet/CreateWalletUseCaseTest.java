package co.com.nequi.walletpocket.wallet;

import co.com.nequi.walletpocket.transaction.exception.BusinessTransactionException;
import co.com.nequi.walletpocket.wallet.gateaway.WalletRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateWalletUseCaseTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private CreateWalletUseCase createWalletUseCase;

    @Test
    @DisplayName("Debe crear y guardar una Wallet exitosamente")
    void shouldCreateWalletSuccessfully() {
        String userId = "samir123";
        Wallet mockWallet = new Wallet(co.com.nequi.walletpocket.wallet.values.WalletId.generate(), userId, java.util.Collections.emptyList());

        when(walletRepository.save(any(Wallet.class))).thenReturn(Mono.just(mockWallet));

        Mono<Wallet> result = createWalletUseCase.execute(userId);

        StepVerifier.create(result)
                .expectNextMatches(wallet -> wallet.userId().equals(userId))
                .verifyComplete();
    }

    @Test
    @DisplayName("Edge Case: Debe transformar el error de persistencia en BusinessTransactionException")
    void shouldHandlePersistenceFailureGracefully() {
        when(walletRepository.save(any(Wallet.class))).thenReturn(Mono.error(new RuntimeException("DB Connection Timeout")));

        Mono<Wallet> result = createWalletUseCase.execute("samir123");

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof BusinessTransactionException
                        && throwable.getMessage().equals("Could not create wallet due to a persistence failure"))
                .verify();
    }
}
