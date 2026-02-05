CREATE TABLE public.texas_holdem_tables(
    id BIGSERIAL PRIMARY KEY,
    max_players INT NOT NULL,
    current_players INT NOT NULL,
    buy_in INT NOT NULL,
    name TEXT NOT NULL
);