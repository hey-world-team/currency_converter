CREATE TABLE IF NOT EXISTS currency_conversion_history (
    id SERIAL PRIMARY KEY,
    conversionDate DATE NOT NULL,
    inputCurrency VARCHAR(3) NOT NULL,
    inputAmount DECIMAL(18, 2) NOT NULL,
    outputCurrency VARCHAR(3) NOT NULL,
    outputAmount DECIMAL(18, 2) NOT NULL
);
