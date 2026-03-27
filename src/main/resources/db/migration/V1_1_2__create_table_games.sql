CREATE TABLE public.games
(
    id                BIGSERIAL PRIMARY KEY,
    max_players       INT       NOT NULL,
    buy_in            INT       NOT NULL,
    name              TEXT      NOT NULL,
    status            INT       NOT NULL,
    pot_id            BIGINT    NOT NULL,
    creator_player_id BIGINT    NOT NULL DEFAULT 0,
    dealer_id         BIGINT    NOT NULL DEFAULT 0,
    active_player_id  BIGINT    NOT NULL DEFAULT 0,
    created_at        TIMESTAMP NOT NULL,
    started_at        TIMESTAMP,
    ended_at          TIMESTAMP,
    CONSTRAINT fk_pot_game FOREIGN KEY (pot_id) REFERENCES public.pots (id)
);