drop table if exists currency cascade;
drop table if exists value cascade;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE currency
(
    id      VARCHAR(3) PRIMARY KEY,
    name    VARCHAR(250) NOT NULL,
    nominal INT
);

CREATE TABLE value
(
    uuid        UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    currency_id VARCHAR(3) REFERENCES currency (id),
    value       NUMERIC,
    date        DATE
);
CREATE TABLE IF NOT EXISTS history (
    id SERIAL PRIMARY KEY,
    conversion_date DATE NOT NULL,
    input_currency VARCHAR(3) NOT NULL,
    input_amount DECIMAL(18, 2) NOT NULL,
    output_currency VARCHAR(3) NOT NULL,
    output_amount DECIMAL(18, 2) NOT NULL
);