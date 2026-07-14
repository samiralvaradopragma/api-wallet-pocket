package co.com.nequi.walletpocket.pocket;

import co.com.nequi.walletpocket.domain.pocket.Pocket;
import co.com.nequi.walletpocket.domain.pocket.values.PocketId;
import co.com.nequi.walletpocket.wallet.gateaway.WalletRepository;
import co.com.nequi.walletpocket.domain.transaction.exception.BusinessTransactionException;
import co.com.nequi.walletpocket.domain.wallet.Wallet;
import co.com.nequi.walletpocket.domain.wallet.exception.WalletNotFoundException;
import co.com.nequi.walletpocket.domain.wallet.values.Money;
import co.com.nequi.walletpocket.domain.wallet.values.WalletId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Collections;

public class AddPocketUseCase {

    private static final Logger log = LoggerFactory.getLogger(AddPocketUseCase.class);
    private final WalletRepository walletRepository;

    public AddPocketUseCase(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Mono<Wallet> execute(WalletId walletId, String pocketName) {
        log.info("Attempting to add pocket '{}' to wallet ID: {}", pocketName, walletId.value());

        return walletRepository.findById(walletId)
                .doOnError(error -> log.error("Database error while searching wallet ID: {}", walletId.value(), error))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Wallet with ID: {} not found for pocket creation", walletId.value());
                    return Mono.error(new WalletNotFoundException("Wallet not found"));
                }))
                .flatMap(wallet -> {
                    Pocket newPocket = new Pocket(PocketId.generate(), pocketName, Money.zero(), Collections.emptyList());
                    Wallet updatedWallet = wallet.addPocket(newPocket);

                    return walletRepository.save(updatedWallet)
                            .doOnSuccess(saved -> log.info("Pocket '{}' successfully linked to wallet ID: {}", pocketName, walletId.value()))
                            .doOnError(error -> log.error("Failed to save wallet after adding pocket '{}'", pocketName, error))
                            .onErrorResume(error -> Mono.error(new BusinessTransactionException("Failed to add pocket due to storage issues")));
                });
    }
}

