package co.com.nequi.walletpocket;

import co.com.nequi.walletpocket.GetMaxExpenseReportUseCase;
import co.com.nequi.walletpocket.domain.pocket.values.PocketId;
import co.com.nequi.walletpocket.domain.transaction.values.Description;
import co.com.nequi.walletpocket.domain.transaction.values.TransactionId;
import co.com.nequi.walletpocket.domain.wallet.values.Money;
import co.com.nequi.walletpocket.domain.wallet.values.WalletId;
import co.com.nequi.walletpocket.dto.DtoRequest;
import co.com.nequi.walletpocket.pocket.AddPocketUseCase;
import co.com.nequi.walletpocket.transaction.ModifyTransactionUseCase;
import co.com.nequi.walletpocket.transaction.RegisterTransactionUseCase;
import co.com.nequi.walletpocket.wallet.CreateWalletUseCase;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.net.URI;

@Component
public class WalletHandler {

    private final CreateWalletUseCase createWalletUseCase;
    private final AddPocketUseCase addPocketUseCase;
    private final RegisterTransactionUseCase registerTransactionUseCase;
    private final ModifyTransactionUseCase modifyTransactionUseCase;
    private final GetMaxExpenseReportUseCase getMaxExpenseReportUseCase;

    public WalletHandler(CreateWalletUseCase createWalletUseCase, AddPocketUseCase addPocketUseCase,
                         RegisterTransactionUseCase registerTransactionUseCase, ModifyTransactionUseCase modifyTransactionUseCase,
                         GetMaxExpenseReportUseCase getMaxExpenseReportUseCase) {

        this.createWalletUseCase = createWalletUseCase;
        this.addPocketUseCase = addPocketUseCase;
        this.registerTransactionUseCase = registerTransactionUseCase;
        this.modifyTransactionUseCase = modifyTransactionUseCase;
        this.getMaxExpenseReportUseCase = getMaxExpenseReportUseCase;

    }

    public Mono<ServerResponse> createWallet(ServerRequest request) {
        return request.bodyToMono(DtoRequest.CreateWallet.class)
                .flatMap(body -> createWalletUseCase.execute(body.userId()))
                .flatMap(wallet -> ServerResponse.created(URI.create("/api/wallets/" + wallet.id().value()))
                        .bodyValue(wallet));
    }

    public Mono<ServerResponse> addPocket(ServerRequest request) {
        WalletId walletId = WalletId.fromString(request.pathVariable("walletId"));
        return request.bodyToMono(DtoRequest.AddPocket.class)
                .flatMap(body -> addPocketUseCase.execute(walletId, body.name()))
                .flatMap(wallet -> ServerResponse.ok().bodyValue(wallet));
    }

    public Mono<ServerResponse> registerTransaction(ServerRequest request) {
        WalletId walletId = WalletId.fromString(request.pathVariable("walletId"));
        PocketId pocketId = PocketId.fromString(request.pathVariable("pocketId"));

        return request.bodyToMono(DtoRequest.RegisterTransaction.class)
                .flatMap(body -> registerTransactionUseCase.execute(
                        walletId,
                        pocketId,
                        new Description(body.description()),
                        new Money(body.amount())
                ))
                .flatMap(wallet -> ServerResponse.ok().bodyValue(wallet));
    }

    public Mono<ServerResponse> modifyTransaction(ServerRequest request) {
        WalletId walletId = WalletId.fromString(request.pathVariable("walletId"));
        PocketId pocketId = PocketId.fromString(request.pathVariable("pocketId"));
        TransactionId transactionId = TransactionId.fromString(request.pathVariable("transactionId"));

        return request.bodyToMono(DtoRequest.ModifyTransaction.class)
                .flatMap(body -> modifyTransactionUseCase.execute(
                        walletId,
                        pocketId,
                        transactionId,
                        new Money(body.amount())
                ))
                .flatMap(wallet -> ServerResponse.ok().bodyValue(wallet));
    }

    public Mono<ServerResponse> getMaxExpenseReport(ServerRequest request) {
        WalletId walletId = WalletId.fromString(request.pathVariable("walletId"));
        return getMaxExpenseReportUseCase.execute(walletId)
                .flatMap(report -> ServerResponse.ok().bodyValue(report));
    }
}
