package co.com.nequi.walletpocket;

import co.com.nequi.walletpocket.dto.DtoRequest;
import co.com.nequi.walletpocket.wallet.Wallet;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/wallets",
                    method = RequestMethod.POST,
                    beanClass = WalletHandler.class,
                    beanMethod = "createWallet",
                    operation = @io.swagger.v3.oas.annotations.Operation(
                            summary = "Crear una nueva Billetera",
                            operationId = "createWallet",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    required = true,
                                    content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = DtoRequest.CreateWallet.class))
                            ),
                            responses = {
                                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Billetera creada exitosamente",
                                            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Wallet.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/wallets/{walletId}/pockets",
                    method = RequestMethod.POST,
                    beanClass = WalletHandler.class,
                    beanMethod = "addPocket",
                    operation = @io.swagger.v3.oas.annotations.Operation(
                            summary = "Agregar un bolsillo a la billetera",
                            operationId = "addPocket",
                            parameters = { @io.swagger.v3.oas.annotations.Parameter(name = "walletId", in = ParameterIn.PATH, required = true) },
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    required = true,
                                    content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = DtoRequest.AddPocket.class))
                            ),
                            responses = {
                                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bolsillo agregado",
                                            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Wallet.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> walletRoutes(WalletHandler handler) {
        return route(POST("/api/wallets"), handler::createWallet)
                .andRoute(POST("/api/wallets/{walletId}/pockets"), handler::addPocket)
                .andRoute(POST("/api/wallets/{walletId}/pockets/{pocketId}/transactions"), handler::registerTransaction)
                .andRoute(PUT("/api/wallets/{walletId}/pockets/{pocketId}/transactions/{transactionId}"), handler::modifyTransaction)
                .andRoute(GET("/api/wallets/{walletId}/pockets/max-expenses"), handler::getMaxExpenseReport);
    }
}