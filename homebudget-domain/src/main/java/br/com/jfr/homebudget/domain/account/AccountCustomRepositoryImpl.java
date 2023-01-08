package br.com.jfr.homebudget.domain.account;

import br.com.jfr.libs.commons.r2dbc.utils.OrderByPaginationBuilder;
import br.com.jfr.libs.commons.r2dbc.utils.QueryHolder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class AccountCustomRepositoryImpl implements AccountCustomRepository {

  private final R2dbcEntityTemplate r2dbcTemplate;
  private final AccountMapper accountMapper;

  @Override
  public Flux<Account> findAll(Pageable page, String search) {

    QueryHolder baseQuery = buildFindAllQuery(search);

    StringBuilder query = new StringBuilder()
        .append("select ")
        .append(" account_id as id, ")
        .append(" code, ")
        .append(" description, ")
        .append(" parent_code as parentCode ")
        .append(baseQuery.getSqlQuery());

    query.append(OrderByPaginationBuilder
                     .buildSortAndPagination(page.getSort(), page.getPageSize(), page.getOffset()));

    log.info("findAll {}", query.toString());

    final var baseQueryExecute = r2dbcTemplate
        .getDatabaseClient()
        .sql(query.toString());

    var queryExecute = baseQueryExecute;
    for (Entry<String, Object> entry : baseQuery.getMapParams().entrySet()) {
      queryExecute = queryExecute.bind(entry.getKey(), entry.getValue());
    }

    return
        queryExecute
            .map(accountMapper::apply)
            .all();
  }

  @Override
  public Mono<Long> findAllCount(String search) {

    QueryHolder baseQuery = buildFindAllQuery(search);

    StringBuilder query = new StringBuilder()
        .append("select ")
        .append(" count(*) ")
        .append(baseQuery.getSqlQuery());

    final var baseQueryExecute = r2dbcTemplate
        .getDatabaseClient()
        .sql(query.toString());

    var queryExecute = baseQueryExecute;
    for (Entry<String, Object> entry : baseQuery.getMapParams().entrySet()) {
      queryExecute = queryExecute.bind(entry.getKey(), entry.getValue());
    }

    return
        queryExecute
            .map(row -> row.get(0, Long.class))
            .first();
  }

  public QueryHolder buildFindAllQuery(String search) {
    Map<String, Object> params = new HashMap<>();

    StringBuilder query = new StringBuilder()
        .append(" from account ");

    if(!ObjectUtils.isEmpty(search)) {
      query.append(" where ")
          .append(" (code like '%' || :search || '%' or ")
          .append(" upper(description) like '%' || upper(:search) || '%') ");
      params.put("search", search);
    }

    return new QueryHolder(query.toString(), params);

  }

  public Flux<Account> findAutocomplete(String code,
                                        String search) {

    Map<String, Object> params = new HashMap<>();

    StringBuilder query = new StringBuilder()
        .append("select ")
        .append(" account_id as id, ")
        .append(" code, ")
        .append(" description, ")
        .append(" parent_code as parentCode ")
        .append("    from account ");

    if (ObjectUtils.isEmpty(code)) {
      query.append("where code like :searchCode or description like :searchDescription ");
      params.put("searchCode", search + "%");
      params.put("searchDescription", "%" + search + "%");
    } else {
      log.info(" findAutocomplete by code {}", code);
      query.append("where code = :code ");
      params.put("code", code);
    }
    query.append(" order by code limit 100");
    log.info("findAll {}", query.toString());

    final var baseQueryExecute = r2dbcTemplate
        .getDatabaseClient()
        .sql(query.toString());

    var queryExecute = baseQueryExecute;
    for (Entry<String, Object> entry : params.entrySet()) {
      queryExecute = queryExecute.bind(entry.getKey(), entry.getValue());
    }

    return
        queryExecute
            .map(accountMapper::apply)
            .all();

  }

}
