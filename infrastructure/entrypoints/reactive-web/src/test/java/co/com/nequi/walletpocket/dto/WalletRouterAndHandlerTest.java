package co.com.nequi.walletpocket.dto;

import co.com.nequi.walletpocket.GetMaxExpenseReportUseCase;
import co.com.nequi.walletpocket.RouterRest;
import co.com.nequi.walletpocket.WalletHandler;
import co.com.nequi.walletpocket.pocket.AddPocketUseCase;
import co.com.nequi.walletpocket.pocket.Pocket;
import co.com.nequi.walletpocket.pocket.values.PocketId;
import co.com.nequi.walletpocket.transaction.ModifyTransactionUseCase;
import co.com.nequi.walletpocket.transaction.RegisterTransactionUseCase;
import co.com.nequi.walletpocket.transaction.Transaction;
import co.com.nequi.walletpocket.transaction.values.Description;
import co.com.nequi.walletpocket.transaction.values.TransactionId;
import co.com.nequi.walletpocket.wallet.CreateWalletUseCase;
import co.com.nequi.walletpocket.wallet.Wallet;
import co.com.nequi.walletpocket.wallet.values.Money;
import co.com.nequi.walletpocket.wallet.values.WalletId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletRouterAndHandlerTest {

    @Mock private CreateWalletUseCase createWalletUseCase;
    @Mock private AddPocketUseCase addPocketUseCase;
    @Mock private RegisterTransactionUseCase registerTransactionUseCase;
    @Mock private ModifyTransactionUseCase modifyTransactionUseCase;
    @Mock private GetMaxExpenseReportUseCase getMaxExpenseReportUseCase;

    private WebTestClient webTestClient;

    private UUID walletUuid;
    private UUID pocketUuid;
    private UUID txUuid;

    @BeforeEach
    void setUp() {
        WalletHandler walletHandler = new WalletHandler(
                createWalletUseCase, addPocketUseCase,
                registerTransactionUseCase, modifyTransactionUseCase,
                getMaxExpenseReportUseCase
        );

        RouterRest routerRest = new RouterRest();
        RouterFunction<ServerResponse> routes = routerRest.walletRoutes(walletHandler);

        this.webTestClient = WebTestClient.bindToRouterFunction(routes).build();

        walletUuid = UUID.randomUUID();
        pocketUuid = UUID.randomUUID();
        txUuid = UUID.randomUUID();
    }

    @Test
    @DisplayName("POST /api/wallets -> Debe retornar 201 Created y el cuerpo de la Wallet")
    void shouldCreateWalletAndReturnCreatedStatus() {
        Wallet mockWallet = new Wallet(new WalletId(walletUuid), "samir123", Collections.emptyList());
        DtoRequest.CreateWallet payload = new DtoRequest.CreateWallet("samir123");

        when(createWalletUseCase.execute("samir123")).thenReturn(Mono.just(mockWallet));

        webTestClient.post()
                .uri("/api/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/api/wallets/" + walletUuid)
                .expectBody()
                .jsonPath("$.userId").isEqualTo("samir123")
                .jsonPath("$.id.value").isEqualTo(walletUuid.toString());
    }

    @Test
    @DisplayName("POST /api/wallets/{id}/pockets -> Debe retornar 200 OK tras añadir un bolsillo")
    void shouldAddPocketSuccessfully() {

        Pocket mockPocket = new Pocket(new PocketId(pocketUuid), "Viajes", Money.zero(), Collections.emptyList());
        Wallet mockWallet = new Wallet(new WalletId(walletUuid), "samir123", List.of(mockPocket));
        DtoRequest.AddPocket payload = new DtoRequest.AddPocket("Viajes");

        when(addPocketUseCase.execute(eq(new WalletId(walletUuid)), eq("Viajes"))).thenReturn(Mono.just(mockWallet));

        webTestClient.post()
                .uri("/api/wallets/{walletId}/pockets", walletUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.pockets[0].name").isEqualTo("Viajes");
    }

    @Test
    @DisplayName("POST /api/wallets/{id}/.../transactions -> Debe registrar transacción y retornar 200 OK")
    void shouldRegisterTransactionSuccessfully() {

        Wallet mockWallet = new Wallet(new WalletId(walletUuid), "samir123", Collections.emptyList());
        DtoRequest.RegisterTransaction payload = new DtoRequest.RegisterTransaction("Depósito", new BigDecimal("150.00"));

        when(registerTransactionUseCase.execute(
                eq(new WalletId(walletUuid)),
                eq(new PocketId(pocketUuid)),
                any(Description.class),
                any(Money.class)
        )).thenReturn(Mono.just(mockWallet));

        webTestClient.post()
                .uri("/api/wallets/{walletId}/pockets/{pocketId}/transactions", walletUuid, pocketUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("PUT /api/wallets/{id}/.../transactions/{txId} -> Debe modificar transacción y retornar 200 OK")
    void shouldModifyTransactionSuccessfully() {

        Wallet mockWallet = new Wallet(new WalletId(walletUuid), "samir123", Collections.emptyList());
        DtoRequest.ModifyTransaction payload = new DtoRequest.ModifyTransaction(new BigDecimal("200.00"));

        when(modifyTransactionUseCase.execute(
                eq(new WalletId(walletUuid)),
                eq(new PocketId(pocketUuid)),
                eq(new TransactionId(txUuid)),
                any(Money.class)
        )).thenReturn(Mono.just(mockWallet));

        webTestClient.put()
                .uri("/api/wallets/{walletId}/pockets/{pocketId}/transactions/{transactionId}", walletUuid, pocketUuid, txUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("GET /api/wallets/{id}/pockets/max-expenses -> Debe retornar el reporte consolidado de gastos máximos")
    void shouldGetMaxExpenseReportSuccessfully() {

        Transaction maxTx = new Transaction(new TransactionId(txUuid), new Description("Vuelos"), new Money(new BigDecimal("950.00")));
        Map<String, Transaction> mockReport = Map.of("Viajes", maxTx);

        when(getMaxExpenseReportUseCase.execute(new WalletId(walletUuid))).thenReturn(Mono.just(mockReport));

        webTestClient.get()
                .uri("/api/wallets/{walletId}/pockets/max-expenses", walletUuid)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.Viajes.amount.value").isEqualTo(950.00)
                .jsonPath("$.Viajes.description.value").isEqualTo("Vuelos");
    }
}