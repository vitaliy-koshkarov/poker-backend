ALTER TABLE public.users ADD COLUMN player_id BIGINT NOT NULL;

ALTER TABLE public.users
ADD CONSTRAINT fk_users_players FOREIGN KEY (player_id) REFERENCES players (id);
