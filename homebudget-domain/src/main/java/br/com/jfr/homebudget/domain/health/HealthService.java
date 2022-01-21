package br.com.jfr.homebudget.domain.health;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class HealthService {

  public Mono<String> checkHealth() {
    return Mono
        .just("is Health");
  }

}
