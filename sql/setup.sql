
-- drop 
drop table flyway_schema_history ;
drop table extract_header_imported ;
drop table baseaudit ;

select pid, datname, usename, application_name, backend_start, query_start, query 
from pg_stat_activity
where application_name = 'sample' 
order by query_start

select now()

select public.gen_random_uuid();
select fn_generate_uuid();

select * from flyway_schema_history fsh 

select * from extract_header_imported ehi 
