ALTER TABLE public.games RENAME TO game_tables;

ALTER TABLE public.game_tables
RENAME COLUMN current_players TO current_players_ids;

ALTER TABLE public.game_tables ALTER COLUMN current_players_ids DROP DEFAULT;

ALTER TABLE public.game_tables
ALTER COLUMN current_players_ids TYPE BIGINT[]
USING ARRAY[current_players_ids]::BIGINT[];

ALTER TABLE public.game_tables
ALTER COLUMN current_players_ids SET DEFAULT '{}';
