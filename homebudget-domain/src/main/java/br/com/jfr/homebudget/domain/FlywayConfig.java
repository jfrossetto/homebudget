package br.com.jfr.homebudget.domain;

import br.com.jfr.libs.commons.r2dbc.flyway.FlywayMigrate;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

  @PostConstruct
  public void migrate() {
    FlywayMigrate.migrate();
  }

}
