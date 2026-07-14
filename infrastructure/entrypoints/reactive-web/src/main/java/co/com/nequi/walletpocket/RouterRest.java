package co.com.nequi.walletpocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> walletRoutes(WalletHandler handler) {
        return route(POST("/api/wallets"), handler::createWallet)
                .andRoute(POST("/api/wallets/{walletId}/pockets"), handler::addPocket)
                .andRoute(POST("/api/wallets/{walletId}/pockets/{pocketId}/transactions"), handler::registerTransaction)
                .andRoute(PUT("/api/wallets/{walletId}/pockets/{pocketId}/transactions/{transactionId}"), handler::modifyTransaction)
                .andRoute(GET("/api/wallets/{walletId}/pockets/max-expenses"), handler::getMaxExpenseReport);
    }
}
