
CREATE ROLE core;
ALTER ROLE core WITH NOSUPERUSER NOINHERIT CREATEROLE NOCREATEDB LOGIN PASSWORD 'password'; --'md5810605c085da5d85eee133466b57fabd';  -- password is "password"
COMMENT ON ROLE core IS 'Owning role of the CoRE Ultra-Structure database';
ALTER ROLE core SET search_path TO ruleform, public, sysadmin, core_admin, readable;
CREATE DATABASE core OWNER core TEMPLATE template_java ENCODING 'UTF8';
