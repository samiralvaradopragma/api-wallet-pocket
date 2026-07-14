package co.com.nequi.walletpoket;

import co.com.nequi.walletpocket.WalletRepositoryAdapter;
import co.com.nequi.walletpocket.entity.PocketEntity;
import co.com.nequi.walletpocket.entity.TransactionEntity;
import co.com.nequi.walletpocket.entity.WalletEntity;
import co.com.nequi.walletpocket.pocket.Pocket;
import co.com.nequi.walletpocket.pocket.values.PocketId;
import co.com.nequi.walletpocket.repository.SpringDataPocketRepository;
import co.com.nequi.walletpocket.repository.SpringDataTransactionRepository;
import co.com.nequi.walletpocket.repository.SpringDataWalletRepository;
import co.com.nequi.walletpocket.transaction.Transaction;
import co.com.nequi.walletpocket.transaction.values.Description;
import co.com.nequi.walletpocket.transaction.values.TransactionId;
import co.com.nequi.walletpocket.wallet.Wallet;
import co.com.nequi.walletpocket.wallet.values.Money;
import co.com.nequi.walletpocket.wallet.values.WalletId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletRepositoryAdapterTest {

    @Mock
    private SpringDataWalletRepository walletRepo;

    @Mock
    private SpringDataPocketRepository pocketRepo;

    @Mock
    private SpringDataTransactionRepository txRepo;

    @InjectMocks
    private WalletRepositoryAdapter walletRepositoryAdapter;

    private UUID walletUuid;
    private UUID pocketUuid;
    private UUID txUuid;
    private WalletId walletId;

    @BeforeEach
    void setUp() {
        walletUuid = UUID.randomUUID();
        pocketUuid = UUID.randomUUID();
        txUuid = UUID.randomUUID();
        walletId = new WalletId(walletUuid);
    }

    @Test
    @DisplayName("Debe buscar una Wallet y mapear de forma reactiva todo su árbol de agregación")
    void shouldFindByIdAndMapFullAggregateTree() {
        // GIVEN
        WalletEntity walletEntity = new WalletEntity(walletUuid, "samir123", true);
        PocketEntity pocketEntity = new PocketEntity(pocketUuid, walletUuid, "Ahorros", new BigDecimal("100.00"), true);
        TransactionEntity txEntity = new TransactionEntity(txUuid, pocketUuid, "Depósito", new BigDecimal("50.00"), true);

        when(walletRepo.findById(walletUuid)).thenReturn(Mono.just(walletEntity));
        when(pocketRepo.findAllByWalletId(walletUuid)).thenReturn(Flux.just(pocketEntity));
        when(txRepo.findAllByPocketId(pocketUuid)).thenReturn(Flux.just(txEntity));

        // WHEN
        Mono<Wallet> result = walletRepositoryAdapter.findById(walletId);

        // THEN
        StepVerifier.create(result)
                .expectNextMatches(wallet ->
                        wallet.id().value().equals(walletUuid) &&
                                wallet.userId().equals("samir123") &&
                                wallet.pockets().size() == 1 &&
                                wallet.pockets().get(0).name().equals("Ahorros") &&
                                wallet.pockets().get(0).transactions().size() == 1
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Edge Case: Debe retornar Mono.empty() si la Wallet no existe en la base de datos")
    void shouldReturnEmptyMonoWhenWalletDoesNotExist() {
        // GIVEN
        when(walletRepo.findById(walletUuid)).thenReturn(Mono.empty());

        // WHEN
        Mono<Wallet> result = walletRepositoryAdapter.findById(walletId);

        // THEN
        StepVerifier.create(result)
                .verifyComplete(); // Un flujo vacío controlado completa limpiamente sin emitir elementos
    }

    @Test
    @DisplayName("Edge Case: Debe mapear correctamente una Wallet si tiene bolsillos pero no transacciones (listas vacías)")
    void shouldMapCorrectlyWhenPocketsHaveNoTransactions() {
        // GIVEN
        WalletEntity walletEntity = new WalletEntity(walletUuid, "samir123", true);
        PocketEntity pocketEntity = new PocketEntity(pocketUuid, walletUuid, "Bolsillo Vacío", BigDecimal.ZERO, true);

        when(walletRepo.findById(walletUuid)).thenReturn(Mono.just(walletEntity));
        when(pocketRepo.findAllByWalletId(walletUuid)).thenReturn(Flux.just(pocketEntity));
        when(txRepo.findAllByPocketId(pocketUuid)).thenReturn(Flux.empty()); // No hay transacciones

        // WHEN
        Mono<Wallet> result = walletRepositoryAdapter.findById(walletId);

        // THEN
        StepVerifier.create(result)
                .expectNextMatches(wallet ->
                        wallet.pockets().size() == 1 &&
                                wallet.pockets().get(0).transactions().isEmpty()
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe guardar de forma reactiva y en cascada una Wallet completa")
    void shouldSaveWalletAggregateInCascade() {
        // GIVEN
        Transaction transaction = new Transaction(new TransactionId(txUuid), new Description("Compra"), new Money(new BigDecimal("20.00")));
        Pocket pocket = new Pocket(new PocketId(pocketUuid), "Gastos", new Money(new BigDecimal("80.00")), List.of(transaction));
        Wallet wallet = new Wallet(walletId, "samir123", List.of(pocket));

        WalletEntity walletEntity = new WalletEntity(walletUuid, "samir123", true);
        PocketEntity pocketEntity = new PocketEntity(pocketUuid, walletUuid, "Gastos", new BigDecimal("80.00"), true);
        TransactionEntity txEntity = new TransactionEntity(txUuid, pocketUuid, "Compra", new BigDecimal("20.00"), true);

        when(walletRepo.existsById(walletUuid)).thenReturn(Mono.just(false));
        when(walletRepo.save(any(WalletEntity.class))).thenReturn(Mono.just(walletEntity));

        when(pocketRepo.existsById(pocketUuid)).thenReturn(Mono.just(false));
        when(pocketRepo.save(any(PocketEntity.class))).thenReturn(Mono.just(pocketEntity));

        when(txRepo.deleteAllByPocketId(pocketUuid)).thenReturn(Mono.empty());
        when(txRepo.save(any(TransactionEntity.class))).thenReturn(Mono.just(txEntity));

        // WHEN
        Mono<Wallet> result = walletRepositoryAdapter.save(wallet);

        // THEN
        StepVerifier.create(result)
                .expectNextMatches(savedWallet -> savedWallet.id().value().equals(walletUuid))
                .verifyComplete();

        // Verificamos que se ejecuten las persistencias requeridas por tu lógica reactiva
        verify(walletRepo, times(1)).save(any(WalletEntity.class));
        verify(pocketRepo, times(1)).save(any(PocketEntity.class));
        verify(txRepo, times(1)).deleteAllByPocketId(pocketUuid);
        verify(txRepo, times(1)).save(any(TransactionEntity.class));
    }
}