ALTER TABLE public.games
    ADD COLUMN dealer_id        BIGINT,
    ADD COLUMN active_player_id BIGINT;
