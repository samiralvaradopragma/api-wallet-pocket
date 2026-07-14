package co.com.nequi.walletpocket.pocket;

import co.com.nequi.walletpocket.wallet.Wallet;
import co.com.nequi.walletpocket.wallet.gateaway.WalletRepository;
import co.com.nequi.walletpocket.wallet.values.WalletId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddPocketUseCaseTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private AddPocketUseCase addPocketUseCase;

    @Test
    @DisplayName("Debe agregar un nuevo bolsillo vacío de forma reactiva")
    void shouldAddPocketSuccessfully() {
        WalletId walletId = WalletId.generate();
        Wallet wallet = new Wallet(walletId, "samir", Collections.emptyList());

        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(Mono.just(wallet));

        Mono<Wallet> result = addPocketUseCase.execute(walletId, "Ahorro Programado");

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }
}