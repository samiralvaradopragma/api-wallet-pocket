package co.com.nequi.walletpocket.repository;


import co.com.nequi.walletpocket.entity.TransactionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
public interface SpringDataTransactionRepository extends ReactiveCrudRepository<TransactionEntity, UUID> {

    Flux<TransactionEntity> findAllByPocketId(UUID pocketId);
    Mono<Void> deleteAllByPocketId(UUID pocketId);
}
