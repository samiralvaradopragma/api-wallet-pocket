package co.com.nequi.walletpocket.domain.wallet.gateaway;

import co.com.nequi.walletpocket.domain.wallet.Wallet;
import co.com.nequi.walletpocket.domain.wallet.values.WalletId;
import reactor.core.publisher.Mono;

public interface WalletRepository {

    Mono<Wallet> save(Wallet wallet);
    Mono<Wallet> findById(WalletId walletId);
}
