package br.com.jfr.homebudget.api.v1.uploadextract;

import br.com.jfr.homebudget.domain.uploadextract.ImportedExtractHeader;
import br.com.jfr.homebudget.domain.uploadextract.ImportedExtractHeaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/upload-extract")
@RequiredArgsConstructor
public class UploadExtractController {

  private final ImportedExtractHeaderService service;

  @PostMapping
  public Mono<ImportedExtractHeader> uploadExtrac(@RequestBody ImportedExtractHeader extractToImport,
                                                  ServerWebExchange exchange) {
    return service.uploadExtract(extractToImport)
        .doOnSuccess(e -> exchange.getResponse().setStatusCode(HttpStatus.CREATED));
  }

}
