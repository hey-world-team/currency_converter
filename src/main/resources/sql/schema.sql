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