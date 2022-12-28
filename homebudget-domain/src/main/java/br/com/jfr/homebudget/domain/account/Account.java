package br.com.jfr.homebudget.domain.account;

import br.com.jfr.libs.commons.r2dbc.AuditableBaseEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "account")
public class Account extends AuditableBaseEntity<UUID> {

  @Id
  @Column("account_id")
  private UUID id;

  @Column("code")
  private String code;

  @Column("description")
  private String description;

  @Column("parent_code")
  private String parentCode;

  @Column("parent")
  private Boolean parent;

  @Column("level")
  private Integer level;

}
