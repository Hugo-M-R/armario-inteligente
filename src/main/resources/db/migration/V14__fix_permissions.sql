-- Ajusta permissões para o usuário conectado no banco atual.
DO
$$
DECLARE
    db_name text := current_database();
    user_name text := current_user;
BEGIN
    EXECUTE format('GRANT ALL PRIVILEGES ON DATABASE %I TO %I', db_name, user_name);
    EXECUTE format('GRANT ALL PRIVILEGES ON SCHEMA public TO %I', user_name);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO %I', user_name);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO %I', user_name);
    EXECUTE format('GRANT ALL PRIVILEGES ON TABLE flyway_schema_history TO %I', user_name);
END
$$;