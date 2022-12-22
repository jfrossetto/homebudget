package br.com.jfr.homebudget;

import br.com.jfr.libs.commons.PropertyLoader;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@PropertySources({
    @PropertySource(value = "classpath:applicationDefault.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "file:${applicationProperties}", ignoreResourceNotFound = true)
})
@ComponentScan({"br.com.jfr.libs"})
public class AppConfig {

  final private Environment env;

  public AppConfig(Environment env) {
    this.env = env;
  }

  @PostConstruct
  public void postConstruct() {
    PropertyLoader.load(env);
    final String applicationTitle =
        Optional.ofNullable(System.getProperty("instanceName")).orElse("HomebudgetAPI");
    final String applicationVersion = "1.0.0";
    final String applicationUrl = "/api/homebudget";
    System.setProperty("application.title", applicationTitle);
    System.setProperty("application.version", applicationVersion);
    System.setProperty("application.url", applicationUrl);

    log.info("running {}", System.getProperty("app.name"));

  }
}
