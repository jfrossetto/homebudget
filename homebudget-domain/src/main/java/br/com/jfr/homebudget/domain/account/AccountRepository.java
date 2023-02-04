package br.com.jfr.homebudget.domain.account;

import java.util.UUID;
import org.springframework.data.r2dbc.repository.Modifying;
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

  @Modifying
  @Query("delete from account where account_id = :id")
  Mono<Long> delete(@Param("id") UUID id);

  @Query("select exists (select 1 from account where parent_code = :code)")
  Mono<Boolean> hasChildAccounts(@Param("code") String  code);

  @Query("select exists (select 1 from account a \n"
      + "                        where code = :code \n"
      + "                          and (:account_id is null or account_id != :account_id)) \n")
  Mono<Boolean> codeExists(@Param("account_id") UUID id,
                           @Param("code") String code);

}
