package br.com.jfr.homebudget.domain.health;

import static br.com.jfr.libs.commons.security.ContextHolder.CONTEXT_HOLDER_KEY;

import br.com.jfr.libs.commons.security.ContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class HealthService {

  public Mono<String> checkHealth() {
    return Mono.deferContextual(
        ctx -> {
          final ContextHolder contextHolder = ctx.get(CONTEXT_HOLDER_KEY);
          return Mono
              .just("is Health");
        });
  }

}
