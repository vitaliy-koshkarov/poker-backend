CREATE TABLE public.game_tables(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    CONSTRAINT fk_game_table_game FOREIGN KEY (game_id) REFERENCES public.games (id)
);