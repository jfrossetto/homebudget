package br.com.jfr.homebudget;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

@Configuration
@PropertySources({
    @PropertySource(value = "classpath:applicationDefault.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "file:${applicationProperties}", ignoreResourceNotFound = true)
})
@ComponentScan({"com.tr.bluemoon.brtap.commons"})
public class AppConfig {

  @Autowired
  private Environment env;

  @PostConstruct
  public void postConstruct() {
  }
}
