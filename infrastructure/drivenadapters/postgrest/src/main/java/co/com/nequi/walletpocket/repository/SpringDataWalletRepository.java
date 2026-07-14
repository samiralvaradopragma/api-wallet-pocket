package co.com.nequi.walletpocket.repository;

import co.com.nequi.walletpocket.entity.WalletEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import java.util.UUID;

public interface SpringDataWalletRepository extends ReactiveCrudRepository<WalletEntity, UUID> {}
