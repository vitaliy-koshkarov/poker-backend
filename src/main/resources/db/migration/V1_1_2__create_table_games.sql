CREATE TABLE public.games
(
    id          BIGSERIAL PRIMARY KEY,
    max_players INT       NOT NULL,
    buy_in      INT       NOT NULL,
    name        TEXT      NOT NULL,
    status      INT       NOT NULL,
    pot_id      BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    CONSTRAINT fk_pot_game FOREIGN KEY (pot_id) REFERENCES public.pots (id)
);