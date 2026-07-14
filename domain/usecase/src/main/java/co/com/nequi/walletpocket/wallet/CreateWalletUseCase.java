package co.com.nequi.walletpocket.wallet;

import co.com.nequi.walletpocket.domain.transaction.exception.BusinessTransactionException;
import co.com.nequi.walletpocket.domain.wallet.Wallet;

import co.com.nequi.walletpocket.domain.wallet.values.WalletId;
import co.com.nequi.walletpocket.wallet.gateaway.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import java.util.Collections;

public class CreateWalletUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateWalletUseCase.class);
    private final WalletRepository walletRepository;

    public CreateWalletUseCase(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Mono<Wallet> execute(String userId) {

        log.info("Initiating wallet creation process for user: {}", userId);
        Wallet newWallet = new Wallet(WalletId.generate(), userId, Collections.emptyList());

        return walletRepository.save(newWallet)
                .doOnSuccess(savedWallet -> log.info("Wallet successfully created with ID: {} for user: {}", savedWallet.id().value(), userId))
                .doOnError(error -> log.error("Critical error saving new wallet for user: {}", userId, error))
                .onErrorResume(error -> Mono.error(new BusinessTransactionException("Could not create wallet due to a persistence failure")));
    }
}