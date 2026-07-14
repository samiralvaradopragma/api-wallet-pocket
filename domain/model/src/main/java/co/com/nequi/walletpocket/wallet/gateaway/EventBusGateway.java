package co.com.nequi.walletpocket.wallet.gateaway;


import co.com.nequi.walletpocket.transaction.Transaction;
import co.com.nequi.walletpocket.wallet.values.WalletId;
import reactor.core.publisher.Mono;

public interface EventBusGateway {

    Mono<Void> publishHighValueTransaction(WalletId walletId, Transaction transaction);

}
