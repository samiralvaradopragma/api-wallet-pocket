package co.com.nequi.walletpocket;

import co.com.nequi.walletpocket.domain.transaction.Transaction;
import co.com.nequi.walletpocket.domain.wallet.exception.WalletNotFoundException;

import co.com.nequi.walletpocket.domain.wallet.values.WalletId;
import co.com.nequi.walletpocket.wallet.gateaway.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Comparator;
import java.util.Map;

public class GetMaxExpenseReportUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetMaxExpenseReportUseCase.class);
    private final WalletRepository walletRepository;

    public GetMaxExpenseReportUseCase(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Mono<Map<String, Transaction>> execute(WalletId walletId) {
        log.info("Generating maximum expense report for wallet ID: {}", walletId.value());

        return walletRepository.findById(walletId)
                .doOnError(error -> log.error("Error fetching wallet ID: {} for max expense report", walletId.value(), error))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Report generation failed: Wallet ID {} does not exist", walletId.value());
                    return Mono.error(new WalletNotFoundException("Wallet not found"));
                }))
                .flatMapMany(wallet -> Flux.fromIterable(wallet.pockets()))
                .flatMap(pocket -> Mono.justOrEmpty(pocket.transactions().stream()
                                .max(Comparator.comparing(t -> t.amount().value())))
                        .map(maxTx -> Map.entry(pocket.name(), maxTx))
                )
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .doOnSuccess(report -> log.info("Maximum expense report successfully compiled for wallet ID: {}. Pockets processed: {}", walletId.value(), report.size()));
    }
}