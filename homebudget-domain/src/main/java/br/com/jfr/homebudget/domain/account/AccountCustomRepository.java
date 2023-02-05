package br.com.jfr.homebudget.domain.account;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountCustomRepository {

  Flux<Account> findAll(Pageable page,
                        String search);

  Mono<Long> findAllCount(String search);

  Flux<Account> findAutocomplete(String code,
                                 String search);

  Mono<Long> findLastCode(String parentCode);
}
