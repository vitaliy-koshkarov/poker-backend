CREATE TABLE public.users
(
    id        BIGSERIAL PRIMARY KEY,
    email     TEXT UNIQUE NOT NULL,
    password  TEXT        NOT NULL,
    role      TEXT        NOT NULL,
    player_id BIGINT      NOT NULL
);

CREATE TABLE public.players
(
    id          BIGSERIAL PRIMARY KEY,
    nickname    TEXT UNIQUE NOT NULL,
    status      INT         NOT NULL,
    chips       INT         NOT NULL,
    current_bet INT         NOT NULL
);

ALTER TABLE public.users
    ADD CONSTRAINT fk_user_player FOREIGN KEY (player_id) REFERENCES players (id);
