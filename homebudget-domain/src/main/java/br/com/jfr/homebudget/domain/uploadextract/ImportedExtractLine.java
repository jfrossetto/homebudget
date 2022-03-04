package br.com.jfr.homebudget.domain.uploadextract;

import br.com.jfr.libs.commons.r2dbc.AuditableBaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@Table(value = "imported_extract_lines")
public class ImportedExtractLine extends AuditableBaseEntity<UUID> {

  @Id
  @Column("imported_extract_lines_id")
  private UUID id;

  @Column("sequential_id")
  private Integer sequentialId;

  @Column("imported_extract_header_id")
  private UUID importedExtractHeaderId;

  @Column("entry_date")
  private LocalDate entryDate;

  @Column("entry_detail")
  private String entryDetail;

  @Column("entry_value")
  private BigDecimal entryValue;

  @Column("entry_hash")
  private String entryHash;

  @Column("checked")
  private boolean checked;

  @Column("checked_lines_id")
  private UUID checkedLineId;

}
