CREATE TABLE public.pots(
    id BIGSERIAL PRIMARY KEY,
    total INT NOT NULL
);

ALTER TABLE public.game_tables ADD COLUMN pot_id BIGINT NOT NULL;

ALTER TABLE public.game_tables
ADD CONSTRAINT fk_pots_game_tables FOREIGN KEY (pot_id) REFERENCES public.pots (id);
