package br.com.jfr.homebudget.domain.account;

import io.r2dbc.spi.Row;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper implements Function<Row, Account> {

  @Override
  public Account apply(Row row) {
    return Account.builder()
        .id(row.get("id", UUID.class))
        .code(row.get("code", String.class))
        .description(row.get("description", String.class))
        .parentCode(row.get("parentCode", String.class))
        .build();
  }
}
