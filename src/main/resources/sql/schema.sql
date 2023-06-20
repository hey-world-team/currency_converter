DROP TABLE IF EXISTS currency;

CREATE TABLE currency
(
    id      VARCHAR(3) PRIMARY KEY,
    name    VARCHAR(250) NOT NULL,
    nominal INT
);

DROP TABLE IF EXISTS value;

CREATE TABLE value
(
    currency_id VARCHAR(3) PRIMARY KEY,
    value       NUMERIC NOT NULL,
    date        DATE    NOT NULL
);