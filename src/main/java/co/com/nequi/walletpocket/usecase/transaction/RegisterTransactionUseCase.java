package co.com.nequi.walletpocket.usecase.transaction;

import co.com.nequi.walletpocket.domain.pocket.Pocket;
import co.com.nequi.walletpocket.domain.pocket.values.PocketId;
import co.com.nequi.walletpocket.domain.transaction.Transaction;
import co.com.nequi.walletpocket.domain.pocket.exception.PocketNotFoundException;
import co.com.nequi.walletpocket.domain.transaction.values.Description;
import co.com.nequi.walletpocket.domain.transaction.values.TransactionId;
import co.com.nequi.walletpocket.domain.wallet.Wallet;
import co.com.nequi.walletpocket.domain.wallet.exception.WalletNotFoundException;
import co.com.nequi.walletpocket.domain.wallet.gateaway.WalletRepository;
import co.com.nequi.walletpocket.domain.wallet.values.Money;
import co.com.nequi.walletpocket.domain.wallet.values.WalletId;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterTransactionUseCase {
    private static final Logger log = LoggerFactory.getLogger(RegisterTransactionUseCase.class);
    private final WalletRepository walletRepository;

    public RegisterTransactionUseCase(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Mono<Wallet> execute(WalletId walletId, PocketId pocketId, Description description, Money amount) {
        return walletRepository.findById(walletId)
                .doOnError(error -> log.error("Critical database error while fetching wallet metadata for ID: {}", walletId.value(), error))
                .switchIfEmpty(Mono.error(new WalletNotFoundException("The requested wallet account does not exist")))
                .flatMap(wallet -> {
                    Pocket targetPocket = wallet.pockets().stream()
                            .filter(p -> p.id().equals(pocketId))
                            .findFirst()
                            .orElseThrow(() -> new PocketNotFoundException("Pocket targeted for transaction was not found"));

                    Transaction transaction = new Transaction(TransactionId.generate(), description, amount);
                    Pocket updatedPocket = targetPocket.addTransaction(transaction);
                    Wallet updatedWallet = wallet.updatePocket(updatedPocket);

                    return walletRepository.save(updatedWallet)
                            .doOnSuccess(saved -> log.info("Transaction successfully processed and consolidated for Wallet: {}", walletId.value()))
                            .doOnError(error -> log.error("Failed to commit wallet state change for ID: {}", walletId.value(), error))
                            .onErrorResume(error -> Mono.error(new RuntimeException("Transaction processing failed due to consistency persistence issues")));
                });
    }
}