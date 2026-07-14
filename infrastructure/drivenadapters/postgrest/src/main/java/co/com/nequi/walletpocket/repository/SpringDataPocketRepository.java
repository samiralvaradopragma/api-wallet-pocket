package co.com.nequi.walletpocket.repository;


import co.com.nequi.walletpocket.entity.PocketEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface SpringDataPocketRepository extends ReactiveCrudRepository<PocketEntity, UUID> {
    Flux<PocketEntity> findAllByWalletId(UUID walletId);
}