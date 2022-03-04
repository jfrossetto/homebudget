package br.com.jfr.homebudget.domain.uploadextract;

import br.com.jfr.libs.commons.r2dbc.AuditableBaseEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(value = "imported_extract_header")
public class ImportedExtractHeader extends AuditableBaseEntity<UUID> {

  @Id
  @Column("imported_extract_header_id")
  private UUID id;

  @Column("import_date")
  private LocalDateTime importDate;

  @Column("description")
  private String description;

  @Column("hash_header")
  private String hashHeader;

  @Column("checked")
  private boolean checked;

}
