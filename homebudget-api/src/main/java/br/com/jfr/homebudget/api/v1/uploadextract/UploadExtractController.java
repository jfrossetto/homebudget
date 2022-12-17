package br.com.jfr.homebudget.api.v1.uploadextract;

import br.com.jfr.homebudget.domain.uploadextract.ImportedExtractHeader;
import br.com.jfr.homebudget.domain.uploadextract.ImportedExtractHeaderService;
import java.nio.ByteBuffer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/upload-extract")
@RequiredArgsConstructor
public class UploadExtractController {

  private final ImportedExtractHeaderService service;

  @PostMapping
  public Mono<ImportedExtractHeader> uploadExtract(@RequestPart(name = "extractHeader") final ImportedExtractHeader extractToImport,
                                                   @RequestPart(name = "extractFile") final Flux<ByteBuffer> extractFile,
                                                   ServerWebExchange exchange) {
    return service.uploadExtract(extractToImport, extractFile)
        .doOnSuccess(e -> exchange.getResponse().setStatusCode(HttpStatus.CREATED));
  }

}
