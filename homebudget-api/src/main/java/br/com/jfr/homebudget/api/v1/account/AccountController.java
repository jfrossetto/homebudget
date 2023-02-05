package br.com.jfr.homebudget.api.v1.account;

import static br.com.jfr.homebudget.api.v1.ApiUtils.buildNotFoundError;

import br.com.jfr.homebudget.domain.account.Account;
import br.com.jfr.homebudget.domain.account.AccountService;
import br.com.jfr.libs.commons.api.pagination.PageFilter;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
//@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, methods = {RequestMethod.GET, RequestMethod.OPTIONS}, allowedHeaders = "*")
@RequestMapping("/v1/account")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService service;

  @GetMapping
  //@Secured(unprotected = true)
  public Mono<Page<Account>> findAll(@Valid PageFilter pageFilter) {
    return service.findAll(pageFilter.toPageable(),
                           pageFilter.getSearch());
        //.delayElement(Duration.ofMillis(1000));
  }

  @GetMapping("/{id}")
  public Mono<Account> findById(@PathVariable UUID id,
                                ServerWebExchange exchange) {
    return service.findById(id)
        .switchIfEmpty(Mono.error(buildNotFoundError("Account", id)))
        .doOnSuccess(e -> exchange.getResponse().setStatusCode(HttpStatus.OK));

  }

  @PostMapping
  public Mono<Account> create(@RequestBody Account newAccount,
                              ServerWebExchange exchange) {
    return service.create(newAccount)
        .doOnSuccess(e -> exchange.getResponse().setStatusCode(HttpStatus.OK));
  }

  @PutMapping("/{id}")
  public Mono<Account> update(@PathVariable UUID id,
                              @RequestBody Account account,
                              ServerWebExchange exchange) {
    return service.findById(id)
        .switchIfEmpty(Mono.error(buildNotFoundError("Account", id)))
        .flatMap(accountToUpdate -> service.update(accountToUpdate, account))
        .doOnSuccess(e -> exchange.getResponse().setStatusCode(HttpStatus.OK));

  }

  @GetMapping("/autocomplete")
  public Flux<Account> findAutocomplete(@RequestParam(required = false, name = "code") String code,
                                        @RequestParam(required = false, name = "search") String search,
                                        ServerWebExchange exchange) {
    return service.findAutocomplete(code, search)
        .doOnComplete(() -> exchange.getResponse().setStatusCode(HttpStatus.OK));

  }

  @GetMapping("/nextcode")
  public Mono<String> findNextCode(@RequestParam("parentCode") String parentCode,
                                   ServerWebExchange exchange) {
    return service.findNextCode(parentCode)
        .doOnSuccess(e -> exchange.getResponse().setStatusCode(HttpStatus.OK));
  }

  @GetMapping("validate-delete/{id}")
  public Mono<Void> validateDelete(@PathVariable UUID id,
                              ServerWebExchange exchange) {
    return service.findById(id)
        .switchIfEmpty(Mono.error(buildNotFoundError("Account", id)))
        .flatMap(accountToDelete -> service.validateDelete(accountToDelete))
        .doOnSuccess(e -> exchange.getResponse().setStatusCode(HttpStatus.OK));
  }

  @DeleteMapping("/{id}")
  public Mono<Boolean> delete(@PathVariable UUID id,
                              ServerWebExchange exchange) {
    return service.findById(id)
        .switchIfEmpty(Mono.error(buildNotFoundError("Account", id)))
        .flatMap(accountToDelete -> service.delete(accountToDelete))
        .doOnSuccess(e -> exchange.getResponse().setStatusCode(HttpStatus.OK));

  }

  @GetMapping("code-exists/{code}")
  public Mono<Boolean> codeExists(@PathVariable String code,
                                  @RequestParam(required = false) UUID id,
                                  ServerWebExchange exchange) {
    return service.codeExists(id, code)
        .doOnSuccess(e -> exchange.getResponse().setStatusCode(HttpStatus.OK));
  }

}
