CREATE TABLE public.rounds
(
    id                       BIGSERIAL PRIMARY KEY,
    game_id                  BIGINT   NOT NULL,
    round_number             INT      NOT NULL,
    last_aggressor_player_id BIGINT   NOT NULL,
    last_max_bet             INT      NOT NULL,
    players_to_act           BIGINT[] NOT NULL,
    CONSTRAINT fk_rounds_game FOREIGN KEY (game_id) REFERENCES public.games (id)
);