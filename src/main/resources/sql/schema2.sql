CREATE TABLE IF NOT EXISTS conversion_history (
    id SERIAL PRIMARY KEY,
    conversion_date DATE NOT NULL,
    input_currency VARCHAR(3) NOT NULL,
    input_amount DECIMAL(18, 2) NOT NULL,
    output_currency VARCHAR(3) NOT NULL,
    output_amount DECIMAL(18, 2) NOT NULL
);
