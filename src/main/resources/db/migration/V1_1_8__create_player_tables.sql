CREATE TABLE public.player_tables(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    table_ids BIGINT[] NOT NULL
);
