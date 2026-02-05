CREATE TABLE public.players(
    id BIGSERIAL PRIMARY KEY,
    nickname TEXT UNIQUE NOT NULL
);