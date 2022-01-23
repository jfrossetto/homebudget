create table baseaudit (
  create_date timestamp(6) without time zone not null default now(),
  change_date timestamp(6) without time zone,
  created_by uuid not null,
  changed_by uuid,
  active boolean default TRUE ,
  inactive_date timestamp(6) without time zone NULL,
  constraint baseaudit_chk1
    check((change_date is not null and changed_by is not null) or (change_date is null and changed_by is null))
);
