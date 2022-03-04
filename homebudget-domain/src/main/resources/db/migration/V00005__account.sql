create table account (
  account_id uuid default fn_generate_uuid(),
  code varchar(20) not null,
  description varchar(255) not null,
  parent_code varchar(20),
  parent boolean default false,
  level smallint,
  CONSTRAINT account_pk PRIMARY KEY (account_id)
) INHERITS (baseaudit);
comment on column account.description is 'description of account';
comment on column account.code is 'code of account';
