package br.com.jfr.homebudget.domain.uploadextract;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ImportedExtractHeaderService {

  private final ImportedExtractHeaderRepository importedExtractHeaderRepository;

  public Mono<ImportedExtractHeader> uploadExtract(ImportedExtractHeader extractHeaderToImport) {
    return Mono.deferContextual(
        ctx -> {
          return importedExtractHeaderRepository.save(extractHeaderToImport);
        });
  }

}
