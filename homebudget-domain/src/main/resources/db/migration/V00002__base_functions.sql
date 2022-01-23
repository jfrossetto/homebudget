CREATE OR REPLACE FUNCTION public.gen_random_uuid()
    RETURNS uuid
    LANGUAGE 'c'
    COST 1
    VOLATILE PARALLEL UNSAFE
AS '$libdir/pgcrypto', 'pg_random_uuid';

CREATE OR REPLACE FUNCTION public.fn_generate_uuid()
    RETURNS uuid
    LANGUAGE 'sql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
select public.gen_random_uuid();
$BODY$;
