CREATE TABLE public.players_seats
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT    NOT NULL,
    player_id   BIGINT    NOT NULL,
    game_id     BIGINT    NOT NULL,
    seat_number INT       NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    CONSTRAINT fk_game_seat_game FOREIGN KEY (game_id) REFERENCES public.games (id)
);