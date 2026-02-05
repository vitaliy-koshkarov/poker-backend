ALTER TABLE texas_holdem_tables RENAME TO games;

ALTER SEQUENCE texas_holdem_tables_id_seq RENAME TO games_id_seq;

ALTER INDEX texas_holdem_tables_pkey RENAME TO games_pkey;

ALTER TABLE public.users DROP COLUMN nickname;

ALTER TABLE games ADD COLUMN status TEXT NOT NULL;
