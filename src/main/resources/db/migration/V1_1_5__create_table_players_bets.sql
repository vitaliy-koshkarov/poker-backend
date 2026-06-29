CREATE TABLE public.players_bets
(
    id        BIGSERIAL PRIMARY KEY,
    pot_id     BIGINT NOT NULL,
    player_id  BIGINT NOT NULL,
    player_bet INT NOT NULL
);