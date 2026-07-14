package co.com.nequi.walletpocket.transaction;

import co.com.nequi.walletpocket.pocket.Pocket;
import co.com.nequi.walletpocket.pocket.exception.PocketNotFoundException;
import co.com.nequi.walletpocket.pocket.values.PocketId;
import co.com.nequi.walletpocket.transaction.values.Description;
import co.com.nequi.walletpocket.transaction.values.TransactionId;
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
class ModifyTransactionUseCaseTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private ModifyTransactionUseCase modifyTransactionUseCase;

    @Test
    @DisplayName("Debe modificar el monto de una transacción existente con éxito")
    void shouldModifyTransactionAmountSuccessfully() {
        WalletId walletId = WalletId.generate();
        PocketId pocketId = PocketId.generate();
        TransactionId txId = TransactionId.generate();

        Transaction tx = new Transaction(txId, new Description("Compra"), new Money(new BigDecimal("10.00")));
        Pocket pocket = new Pocket(pocketId, "Regalos", new Money(new BigDecimal("10.00")), List.of(tx));
        Wallet wallet = new Wallet(walletId, "user", List.of(pocket));

        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(Mono.just(wallet));

        Mono<Wallet> result = modifyTransactionUseCase.execute(walletId, pocketId, txId, new Money(new BigDecimal("25.00")));

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Edge Case: Debe lanzar PocketNotFoundException si el bolsillo destino no existe")
    void shouldFailWhenPocketNotFound() {
        WalletId walletId = WalletId.generate();
        Wallet wallet = new Wallet(walletId, "user", List.of()); // Sin bolsillos

        when(walletRepository.findById(walletId)).thenReturn(Mono.just(wallet));

        Mono<Wallet> result = modifyTransactionUseCase.execute(walletId, PocketId.generate(), TransactionId.generate(), new Money(BigDecimal.TEN));

        StepVerifier.create(result)
                .expectError(PocketNotFoundException.class)
                .verify();
    }
}