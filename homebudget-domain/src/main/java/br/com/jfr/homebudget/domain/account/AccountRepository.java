package br.com.jfr.homebudget.domain.account;

import java.util.UUID;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account, UUID>, AccountCustomRepository {

  @Query("select * from account "
      + " where account_id = :id")
  Mono<Account> findById(@Param("id") UUID id);

}
