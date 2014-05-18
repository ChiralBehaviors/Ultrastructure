
--CREATE ROLE core;
ALTER ROLE core WITH NOSUPERUSER NOINHERIT CREATEROLE NOCREATEDB LOGIN PASSWORD 'password'; --'md5810605c085da5d85eee133466b57fabd';  -- password is "password"
COMMENT ON ROLE core IS 'Owning role of the CoRE Ultra-Structure database';
ALTER ROLE core SET search_path TO ruleform, public, sysadmin, core_admin, readable;

CREATE ROLE core_java;
ALTER ROLE core_java WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN PASSWORD 'password'; -- 'md5821a71888b905627e8147ee8ce8c8293'; -- password is "password"
COMMENT ON ROLE core_java IS 'Login role intended for use by Java programs that need to interact with the CoRE database';

CREATE ROLE core_read_only;
ALTER ROLE core_read_only WITH NOSUPERUSER NOINHERIT NOCREATEROLE NOCREATEDB NOLOGIN;
COMMENT ON ROLE core_read_only IS 'Group in which to place users that need only read only access to the CoRE database';

CREATE ROLE core_read_write;
ALTER ROLE core_read_write WITH NOSUPERUSER NOINHERIT NOCREATEROLE NOCREATEDB NOLOGIN;
COMMENT ON ROLE core_read_write IS 'Group in which to place users that need basic CRUD access to the CoRE database';
ALTER ROLE core_read_write SET search_path TO ruleform;

CREATE ROLE core_users;
ALTER ROLE core_users WITH NOSUPERUSER NOINHERIT NOCREATEROLE NOCREATEDB NOLOGIN;
COMMENT ON ROLE core_users IS 'Group for roles that should never be able to esclate to ownership privileges in the CoRE database';

GRANT core_users TO core_read_only GRANTED BY postgres;
GRANT core_users TO core_read_write GRANTED BY postgres;
GRANT core_read_write TO core_java GRANTED BY postgres;
