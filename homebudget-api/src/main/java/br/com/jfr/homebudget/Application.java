package br.com.jfr.homebudget;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.reactive.config.EnableWebFlux;

@Slf4j
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, FlywayAutoConfiguration.class})
@EnableWebFlux
public class Application {

  // Constructor to force injection order of priorities
  public Application(AppConfig config) {}

  public static void main(final String[] args) {
    log.info("Starting application");

    SpringApplication app = new SpringApplication(Application.class);
    app.setBannerMode(Banner.Mode.OFF);
    app.run(args);
  }
}
