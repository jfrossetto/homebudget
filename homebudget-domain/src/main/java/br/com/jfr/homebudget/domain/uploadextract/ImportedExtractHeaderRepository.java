package br.com.jfr.homebudget.domain.uploadextract;

import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportedExtractHeaderRepository extends ReactiveCrudRepository<ImportedExtractHeader, UUID> {

}
