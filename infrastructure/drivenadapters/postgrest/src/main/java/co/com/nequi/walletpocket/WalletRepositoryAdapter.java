package co.com.nequi.walletpocket;


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
import co.com.nequi.walletpocket.wallet.gateaway.WalletRepository;
import co.com.nequi.walletpocket.wallet.values.Money;
import co.com.nequi.walletpocket.wallet.values.WalletId;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class WalletRepositoryAdapter implements WalletRepository {

    private final SpringDataWalletRepository walletRepo;
    private final SpringDataPocketRepository pocketRepo;
    private final SpringDataTransactionRepository txRepo;

    public WalletRepositoryAdapter(SpringDataWalletRepository walletRepo, SpringDataPocketRepository pocketRepo, SpringDataTransactionRepository txRepo) {
        this.walletRepo = walletRepo;
        this.pocketRepo = pocketRepo;
        this.txRepo = txRepo;
    }

    @Override
    public Mono<Wallet> findById(WalletId walletId) {
        return walletRepo.findById(walletId.value())
                .flatMap(walletEntity -> pocketRepo.findAllByWalletId(walletEntity.getId())
                        .flatMap(pocketEntity -> txRepo.findAllByPocketId(pocketEntity.getId())
                                .map(t -> new Transaction(
                                        new TransactionId(t.getId()),
                                        new Description(t.getDescription()),
                                        new Money(t.getAmount())
                                ))
                                .collectList()
                                .map(txs -> new Pocket(
                                        new PocketId(pocketEntity.getId()),
                                        pocketEntity.getName(),
                                        new Money(pocketEntity.getBalance()),
                                        txs
                                )))
                        .collectList()
                        .map(pockets -> new Wallet(
                                new WalletId(walletEntity.getId()),
                                walletEntity.getUserId(),
                                pockets
                        )));
    }

    @Override
    public Mono<Wallet> save(Wallet wallet) {
        return walletRepo.existsById(wallet.id().value())
                .flatMap(exists -> {
                    WalletEntity entity = WalletEntity.builder()
                            .id(wallet.id().value())
                            .userId(wallet.userId())
                            .isNewEntity(!exists)
                            .build();

                    return walletRepo.save(entity);
                })
                .flatMap(w -> Flux.fromIterable(wallet.pockets())
                        .flatMap(p -> {
                            return pocketRepo.existsById(p.id().value())
                                    .flatMap(pExists -> pocketRepo.save(PocketEntity.builder()
                                            .id(p.id().value())
                                            .walletId(w.getId())
                                            .name(p.name())
                                            .balance(p.balance().value())
                                            .isNewEntity(!pExists)
                                            .build()));
                        })
                        .flatMap(pe -> txRepo.deleteAllByPocketId(pe.getId())
                                .thenMany(Flux.fromIterable(wallet.pockets()))
                                .filter(p -> p.id().value().equals(pe.getId()))
                                .flatMap(p -> Flux.fromIterable(p.transactions()))
                                .flatMap(t -> txRepo.save(TransactionEntity.builder()
                                        .id(t.id().value())
                                        .pocketId(pe.getId())
                                        .description(t.description().value())
                                        .amount(t.amount().value())
                                        .isNewEntity(true)
                                        .build()))
                                .then(Mono.just(pe)))
                        .then(Mono.just(wallet)));
    }
}