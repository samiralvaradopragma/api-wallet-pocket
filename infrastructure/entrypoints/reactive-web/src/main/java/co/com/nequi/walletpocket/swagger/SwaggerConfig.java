package co.com.nequi.walletpocket.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI walletPocketOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Wallet Pocket API")
                        .description("Specification of reactive endpoints for wallet and pocket management")
                        .version("1.0.0"));
    }
}