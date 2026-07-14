package co.com.nequi.walletpocket.transaction;

import co.com.nequi.walletpocket.domain.pocket.Pocket;
import co.com.nequi.walletpocket.domain.pocket.exception.PocketNotFoundException;
import co.com.nequi.walletpocket.domain.pocket.values.PocketId;
import co.com.nequi.walletpocket.domain.transaction.Transaction;
import co.com.nequi.walletpocket.domain.transaction.exception.BusinessTransactionException;
import co.com.nequi.walletpocket.domain.transaction.values.TransactionId;
import co.com.nequi.walletpocket.domain.wallet.Wallet;
import co.com.nequi.walletpocket.domain.wallet.exception.WalletNotFoundException;
import co.com.nequi.walletpocket.domain.wallet.values.Money;
import co.com.nequi.walletpocket.domain.wallet.values.WalletId;
import co.com.nequi.walletpocket.wallet.gateaway.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class ModifyTransactionUseCase {

    private static final Logger log = LoggerFactory.getLogger(ModifyTransactionUseCase.class);
    private final WalletRepository walletRepository;

    public ModifyTransactionUseCase(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Mono<Wallet> execute(WalletId walletId, PocketId pocketId, TransactionId transactionId, Money newAmount) {
        log.info("Modifying transaction ID: {} in pocket ID: {} for wallet ID: {}", transactionId.value(), pocketId.value(), walletId.value());

        return walletRepository.findById(walletId)
                .doOnError(error -> log.error("Error retrieving wallet ID: {} during modification", walletId.value(), error))
                .switchIfEmpty(Mono.error(new WalletNotFoundException("Wallet not found")))
                .flatMap(wallet -> {
                    Pocket targetPocket = wallet.pockets().stream()
                            .filter(p -> p.id().equals(pocketId))
                            .findFirst()
                            .orElseThrow(() -> new PocketNotFoundException("Pocket not found"));

                    Transaction targetTransaction = targetPocket.transactions().stream()
                            .filter(t -> t.id().equals(transactionId))
                            .findFirst()
                            .orElseThrow(() -> new BusinessTransactionException("Transaction not found within the specified pocket"));

                    Transaction updatedTransaction = targetTransaction.updateAmount(newAmount);
                    Pocket updatedPocket = targetPocket.modifyTransaction(updatedTransaction);
                    Wallet updatedWallet = wallet.updatePocket(updatedPocket);

                    return walletRepository.save(updatedWallet)
                            .doOnSuccess(saved -> log.info("Transaction ID: {} successfully updated with new amount", transactionId.value()))
                            .doOnError(error -> log.error("Error persisting transaction modification for ID: {}", transactionId.value(), error));
                });
    }
}