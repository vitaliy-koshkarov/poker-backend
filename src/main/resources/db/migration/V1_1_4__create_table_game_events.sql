CREATE TABLE public.game_events
(
    id              BIGSERIAL PRIMARY KEY,
    game_id         BIGINT    NOT NULL,
    user_id         BIGINT    NOT NULL,
    player_id       BIGINT    NOT NULL,
    pot_it          BIGINT    NOT NULL,
    type            INT       NOT NULL,
    game_event_data JSONB     NOT NULL,
    created_at      TIMESTAMP NOT NULL
);