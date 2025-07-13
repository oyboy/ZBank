CREATE TABLE market_data
(
    id                BIGSERIAL PRIMARY KEY,
    event_id          VARCHAR(255) NOT NULL,
    market_type_id    BIGINT       NOT NULL,
    selection_type_id BIGINT       NOT NULL,
    price             DOUBLE PRECISION,
    probability       DOUBLE PRECISION,
    status            VARCHAR(255)
);
