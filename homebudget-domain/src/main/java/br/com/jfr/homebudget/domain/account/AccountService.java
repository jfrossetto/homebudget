package br.com.jfr.homebudget.domain.account;

import br.com.jfr.libs.commons.exception.BusinessException;
import br.com.jfr.libs.commons.exception.Error;
import br.com.jfr.libs.commons.exception.ErrorDetail;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
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

  @Transactional
  public Mono<Account> update(Account accountToUpdate,
                              Account account) {
    return Mono.deferContextual(
        ctx -> {
          accountToUpdate.setDescription(account.getDescription());
          accountToUpdate.setParentCode(account.getParentCode());
          return repository.save(accountToUpdate);
        });
  }

  @Transactional
  public Mono<Account> create(Account newAccount) {
    return Mono.deferContextual(
        ctx -> {
          newAccount.setId(null);
          return repository.save(newAccount)
              .onErrorResume(ex -> Mono
                  .error(new BusinessException(String.format("Error saving account %s", ex.getMessage()), ex)));
        });
  }

  public Flux<Account> findAutocomplete(String code, String search) {
    return Flux.deferContextual(
        ctx -> {
          return repository.findAutocomplete(code, ObjectUtils.isEmpty(search) ? "" : search);
        });
  }

  public Mono<String> findNextCode(String parentCode) {
    //ToDo define parameter to struct code ex. 00.00.000 and increment of each level
    //     until ... level 0 increment 10 , level
    return Mono.deferContextual(
        ctx -> {
          return repository.findLastCode(parentCode)
              .map(last -> ObjectUtils.isEmpty(parentCode) ? last + 10L : last +1L)
              .map(last -> padZeros(last,
                                    ObjectUtils.isEmpty(parentCode) || parentCode.length() < 4 ? 2 : 3))
              .map(lastPaded -> parentCode.concat(lastPaded));
        });
  }

  private String padZeros(Long value, int length) {
    return String.format("%1$" + length + "s", value).replace(' ', '0');
        //String.format("0%"+len+"d", value);
  }

  public Mono<Void> validateDelete(Account account) {
    return Mono.deferContextual(
        ctx -> {
          return checksToDelete(account)
              .filter(errorDetails -> !CollectionUtils.isEmpty(errorDetails))
              .flatMap(errorDetails -> Mono.error(new BusinessException(
                  new Error("validationError", "cannotDelete").addDetails(errorDetails))))
              .then();
        });
  }

  private Mono<List<ErrorDetail>> checksToDelete(Account account) {

    Mono<List<ErrorDetail>> hasChild = Mono.just(new ArrayList<ErrorDetail>())
        .flatMap(errorDetails -> repository
            .hasChildAccounts(account.getCode())
            .map(has -> {
              if (has) {
                errorDetails.add(new ErrorDetail("hasChild", "is parent from other accounts"));
              }
              return errorDetails;
            }));

    //ToDo check if has entries in extracts ... use Mono.zip(hasChild, hasEntries ...)
    return hasChild;
  }

  @Transactional
  public Mono<Boolean> delete(Account account) {
    return Mono.deferContextual(
        ctx -> {
          return repository.delete(account.getId())
              .map(recordsCount -> recordsCount != 0L)
              .onErrorResume(ex -> Mono
                  .error(new BusinessException(String.format("Error deleting account %s", ex.getMessage()), ex)));
        });
  }

  public Mono<Boolean> codeExists(UUID id, String code) {
    return Mono.deferContextual(
        ctx -> {
          return repository.codeExists(id, code);
        });
  }
}
