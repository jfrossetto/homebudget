
-- drop 
drop table flyway_schema_history ;
drop table extract_header_imported ;
drop table baseaudit ;

select pid, datname, usename, application_name, backend_start, query_start, query 
from pg_stat_activity
--where application_name = 'sample' 
order by query_start

select now()

select public.gen_random_uuid();
select fn_generate_uuid();

select * from flyway_schema_history fsh 

select * from imported_extract_header ieh
select * from imported_extract_lines iel 

select * from account 

select account_id as id, 
       code, 
       description, 
       parent_code as parentCode
 from account 
where description like '%a%'

insert into account(code, description, parent_code, parent, level, created_by) values('10', 'Receitas', '', true, 1, '00000000-0000-0000-0000-000000000000');

insert into account(code, description, parent_code, parent, level, created_by) values('1001',	'Salários', '10', true, 2, '00000000-0000-0000-0000-000000000000');
insert into account(code, description, parent_code, parent, level, created_by) values('1001001',	'salario jebs', '1001', false, 3, '00000000-0000-0000-0000-000000000000');
insert into account(code, description, parent_code, parent, level, created_by) values('1001002',	'adiantamento jebs', '1001', false, 3, '00000000-0000-0000-0000-000000000000');
insert into account(code, description, parent_code, parent, level, created_by) values('1001003',	'pagto cintia','1001', false, 3, '00000000-0000-0000-0000-000000000000');
insert into account(code, description, parent_code, parent, level, created_by) values('1001004',	'va','1001', false, 3, '00000000-0000-0000-0000-000000000000');
insert into account(code, description, parent_code, parent, level, created_by) values('1001005',	'vr','1001', false, 3, '00000000-0000-0000-0000-000000000000');
insert into account(code, description, parent_code, parent, level, created_by) values('1001006',	'alelo','1001', false, 3, '00000000-0000-0000-0000-000000000000');
insert into account(code, description, parent_code, parent, level, created_by) values('1001007',	'pagtos hopi','1001' , false, 3, '00000000-0000-0000-0000-000000000000');
insert into account(code, description, parent_code, parent, level, created_by) values('1001008',	'13o.','1001', false, 3, '00000000-0000-0000-0000-000000000000');
