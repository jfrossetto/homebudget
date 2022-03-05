package br.com.jfr.homebudget.api.v1.account;

import br.com.jfr.homebudget.domain.account.Account;
import br.com.jfr.homebudget.domain.account.AccountService;
import br.com.jfr.libs.commons.api.pagination.PageFilter;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/account")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService service;

  @GetMapping
  public Mono<Page<Account>> findAll(@Valid PageFilter pageFilter) {
    return service.findAll(pageFilter.toPageable(),
                           pageFilter.getSearch());
  }

}
