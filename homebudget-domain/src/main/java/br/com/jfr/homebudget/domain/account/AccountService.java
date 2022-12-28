package br.com.jfr.homebudget.domain.account;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository repository;

  public Mono<Page<Account>> findAll(Pageable page,
                                     String search) {
    return Mono.deferContextual(
        ctx -> {
          return repository.findAll(page, search)
              .collectList()
              .zipWith(repository.findAllCount(search))
              .map(tuple2 -> PageableExecutionUtils.getPage(tuple2.getT1(), page, () -> tuple2.getT2()));
        });
  }

  public Mono<Account> findById(UUID id) {
    return Mono.deferContextual(
        ctx -> {
          return repository.findById(id);
        });
  }

  public Mono<Account> update(Account accountToUpdate,
                              Account account) {
    return Mono.deferContextual(
        ctx -> {
          accountToUpdate.setDescription(account.getDescription());
          return repository.save(accountToUpdate);
        });
  }
}
