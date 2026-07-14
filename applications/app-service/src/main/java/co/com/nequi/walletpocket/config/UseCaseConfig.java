package co.com.nequi.walletpocket.config;

import co.com.nequi.walletpocket.GetMaxExpenseReportUseCase;
import co.com.nequi.walletpocket.pocket.AddPocketUseCase;
import co.com.nequi.walletpocket.transaction.ModifyTransactionUseCase;
import co.com.nequi.walletpocket.transaction.RegisterTransactionUseCase;
import co.com.nequi.walletpocket.wallet.CreateWalletUseCase;
import co.com.nequi.walletpocket.wallet.gateaway.WalletRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateWalletUseCase createWalletUseCase(WalletRepository walletRepository) {
        return new CreateWalletUseCase(walletRepository);
    }

    @Bean
    public AddPocketUseCase addPocketUseCase(WalletRepository walletRepository) {
        return new AddPocketUseCase(walletRepository);
    }

    @Bean
    public RegisterTransactionUseCase registerTransactionUseCase(WalletRepository walletRepository) {
        return new RegisterTransactionUseCase(walletRepository);
    }

    @Bean
    public ModifyTransactionUseCase modifyTransactionUseCase(WalletRepository walletRepository) {
        return new ModifyTransactionUseCase(walletRepository);
    }

    @Bean
    public GetMaxExpenseReportUseCase getMaxExpenseReportUseCase(WalletRepository walletRepository) {
        return new GetMaxExpenseReportUseCase(walletRepository);
    }
}