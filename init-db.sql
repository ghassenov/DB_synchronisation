-- Initializes the databases used by the three modules.
-- This runs only on first container initialization (when the postgres data volume is empty).
-- The blocks below are written to be safe if re-run.

DO $$
BEGIN
	IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'bo1_db') THEN
		EXECUTE 'CREATE DATABASE bo1_db';
	END IF;
END
$$;

DO $$
BEGIN
	IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'bo2_db') THEN
		EXECUTE 'CREATE DATABASE bo2_db';
	END IF;
END
$$;

DO $$
BEGIN
	IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'ho_db') THEN
		EXECUTE 'CREATE DATABASE ho_db';
	END IF;
END
$$;
