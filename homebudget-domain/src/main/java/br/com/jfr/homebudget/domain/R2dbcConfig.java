package br.com.jfr.homebudget.domain;

import br.com.jfr.libs.commons.r2dbc.PostgresR2dbcConnectionPoolFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableR2dbcRepositories(basePackages = "br.com.jfr.homebudget.domain")
@EnableTransactionManagement
@EnableR2dbcAuditing
public class R2dbcConfig extends AbstractR2dbcConfiguration {

  @Bean("r2dbcConnectionFactory")
  @Override
  public ConnectionFactory connectionFactory() {
    return new PostgresR2dbcConnectionPoolFactory().create();
  }

  @Bean("r2dbcTransactionManager")
  ReactiveTransactionManager transactionManager(
      @Qualifier("r2dbcConnectionFactory") ConnectionFactory connectionFactory) {
    return new R2dbcTransactionManager(connectionFactory);
  }

}
