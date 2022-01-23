create table imported_extract_header (
  imported_extract_header_id uuid default fn_generate_uuid(),
  import_date timestamp(6) without time zone,
  description varchar(255),
  hash_header varchar(255),
  checked boolean,
  CONSTRAINT imported_extract_header_pk PRIMARY KEY (imported_extract_header_id)
) INHERITS (baseaudit);
comment on column imported_extract_header.description is 'description of extract';
comment on column imported_extract_header.hash_header is 'hash of imported extract';

create table imported_extract_lines (
  imported_extract_lines_id uuid default fn_generate_uuid(),
  sequential_id smallint not null,
  imported_extract_header_id uuid not null,
  entry_date date not null,
  entry_detail varchar(255) not null,
  entry_value numeric(9,2) not null,
  entry_hash varchar(255),
  checked boolean,
  checked_lines_id uuid,
  CONSTRAINT imported_extract_lines_pk PRIMARY KEY (imported_extract_lines_id),
  constraint imported_extract_lines_fk1 foreign key (imported_extract_header_id) references imported_extract_header(imported_extract_header_id)
) INHERITS (baseaudit);
