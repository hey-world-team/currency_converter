package com.github.hey_world_team.currency_converter.repository;

import com.github.hey_world_team.currency_converter.model.Currency;
import com.github.hey_world_team.currency_converter.model.CurrencyConversionHistory;
import com.github.hey_world_team.currency_converter.repository.mapper.CurrencyMapper;
import com.github.hey_world_team.currency_converter.repository.mapper.CurrencyHistoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

/**
 * CurrencyRepositoryImpl provides implementation of methods for working with the data store
 */
@Repository
public class CurrencyRepositoryImpl implements CurrencyRepository, CurrencyConversionRepository {

    private static final Logger log = LoggerFactory.getLogger(CurrencyRepositoryImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CurrencyRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * This method saves Currency object in the data store
     * It executes SQL queries to insert the currency and its value into the currency and value tables
     *
     * @param currency
     * @return the id of the saved currency. Otherwise, it throws a RuntimeException
     */
    @Override
    public String saveCurrency(Currency currency) {
        String insertQuery = "WITH inserted_currency AS (" +
                "    INSERT INTO currency (id, name, nominal) " +
                "    VALUES (?, ?, ?) " +
                "    RETURNING id" +
                ") " +
                "INSERT INTO value (uuid,   currency_id, value, date) " +
                "VALUES (uuid_generate_v4(), (SELECT id FROM inserted_currency), ?, ?)";

        int rowsAffected = jdbcTemplate.update(insertQuery,
                currency.getId(),
                currency.getName(),
                currency.getNominal(),
                currency.getValue().getValue(),
                currency.getValue().getDate());

        if (rowsAffected > 0) {
            return currency.getId();
        } else {
            log.error("Failed to save currency with id: {}", currency.getId());
            throw new RuntimeException("Failed to save currency with id: " + currency.getId());
        }
    }

    /**
     * This method stores the list of Currency objects in the data store
     *
     * @param currencies
     * @return the number of saved records
     */
    @Override
    public int saveCurrencies(List<Currency> currencies) {
        String insertQuery = "WITH inserted_currency AS (" +
                "    INSERT INTO currency (id, name, nominal) " +
                "    VALUES (?, ?, ?) " +
                "    RETURNING id" +
                ") " +
                "INSERT INTO value (currency_id, value, date) " +
                "VALUES ((SELECT id FROM inserted_currency), ?, ?)";

        int rowsAffected = jdbcTemplate.batchUpdate(insertQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                Currency currency = currencies.get(i);

                preparedStatement.setString(1, currency.getId());
                preparedStatement.setString(2, currency.getName());
                preparedStatement.setInt(3, currency.getNominal());
                preparedStatement.setBigDecimal(4, currency.getValue().getValue());
                preparedStatement.setDate(5, Date.valueOf(currency.getValue().getDate()));
            }

            @Override
            public int getBatchSize() {
                return currencies.size();
            }
        }).length;

        if (rowsAffected > 0) {
            return rowsAffected;
        } else {
            log.error("Failed to save currencies");
            throw new RuntimeException("Failed to save currencies");
        }
    }

    /**
     * This method returns a Currency object with the specified ID found in the data store
     * It executes a SQL query to retrieve currency data by currency ID
     * If the currency is not found, returns null
     *
     * @param id
     */
    @Override
    public Currency getCurrencyById(String id) {
        String selectQuery = "SELECT c.id, c.name, c.nominal, v.value, v.date " +
                "FROM currency c " +
                "JOIN value v ON c.id = v.currency_id " +
                "WHERE v.currency_id = ?";
        try {
            return jdbcTemplate.queryForObject(selectQuery, new CurrencyMapper(), id);
        } catch (EmptyResultDataAccessException ex) {
            log.info("Currency with id {} not found", id);
            return null;
        }
    }

    /**
     * This method updates the Currency object in the data store
     * It executes an SQL query to update the value and date of the currency in the value table by its ID
     * If the update is successful, returns the updated Currency object. Otherwise, it throws a RuntimeException
     *
     * @param currency
     */
    @Override
    public Currency updateCurrency(Currency currency) {
        String updateQuery = "update value set value = ?, date = ?  WHERE currency_id = ?";
        int rowsAffected = 0;
        try {

            rowsAffected = jdbcTemplate.update(updateQuery,
                    currency.getValue().getValue(),
                    currency.getValue().getDate(),
                    currency.getId());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }

        if (rowsAffected > 0) {
            return currency;
        } else {
            log.error("Failed to update currency with id: {}", currency.getId());
            throw new RuntimeException("Failed to update currency with id: " + currency.getId());
        }
    }

    /**
     * This method updates the list of Currency objects in the data store
     *
     * @param currencies
     * @return the number of updated records
     */
    @Override
    public int updateCurrencies(List<Currency> currencies) {
        String updateQuery = "update value set value = ?, date = ?  WHERE currency_id = ?";
        int rowsAffected = jdbcTemplate.batchUpdate(updateQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Currency currency = currencies.get(i);
                ps.setBigDecimal(1, currency.getValue().getValue());
                if (currency.getValue().getDate() != null) {
                    ps.setDate(2, Date.valueOf(currency.getValue().getDate()));
                } else {
                    ps.setDate(2, null);
                }
                ps.setString(3, currency.getId());
            }

            @Override
            public int getBatchSize() {
                return currencies.size();
            }
        }).length;
        if (rowsAffected > 0) {
            return rowsAffected;
        } else {
            log.error("Failed to update currencies");
            throw new RuntimeException("Failed to update currencies");
        }
    }

    /**
     * This method returns a list of currencies in the specified period for the specified currency identifiers
     *
     * @param startDate
     * @param endDate
     * @param idFirst
     * @param idSecond
     */
    @Override
    public List<Currency> getCurrencyByPeriod(LocalDate startDate, LocalDate endDate, String idFirst, String idSecond) {
        String selectQuery =
                "SELECT c.id, c.name, c.nominal, v.value, v.date " +
                        "FROM currency c " +
                        "JOIN value v ON c.id = v.currency_id " +
                        "WHERE (id = ? OR id = ?) AND " +
                        "date >= ? AND " +
                        "date <= ?";
        List<Currency> currencies = new ArrayList<>();
        try {
            currencies = jdbcTemplate.query(selectQuery, new CurrencyMapper(), idFirst, idSecond, startDate, endDate);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
        return currencies;
    }

    /**
     * This method returns a list of all currencies for a given date
     *
     * @param date
     * @return list of Currency objects
     */
    @Override
    public List<Currency> getAllCurrency(LocalDate date) {
        String selectQuery = "SELECT c.id, c.name, c.nominal, v.value, v.date " +
                "FROM currency c " +
                "JOIN value v ON c.id = v.currency_id where v.date = ? AND v.currency_id != 'RUB'";

        return new ArrayList<>(jdbcTemplate.query(selectQuery, new CurrencyMapper(), date));
    }

    /**
     * This method returns a list of identifiers of all currencies
     */
    @Override
    public List<String> getAllCurrenciesIds() {
        return new ArrayList<>(jdbcTemplate.query("select id from currency",
                (rs, rowNum) -> rs.getString("id")));
    }

    /**
     * This method checks repository is empty or not
     *
     * @return true or false
     */
    @Override
    public boolean isEmpty() {
        String countQuery = "select COUNT(*) from currency";
        Integer count = jdbcTemplate.queryForObject(countQuery, Integer.class);
        return count != null && count == 0;

    }

    @Override
    public String saveCurrencyConversion(CurrencyConversionHistory history) {
        String insertQuery = "INSERT INTO history (conversion_date, input_currency, input_amount, output_currency, output_amount) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, new java.sql.Date(history.getConversionDate().getTime()));
            ps.setString(2, history.getInputCurrency());
            ps.setBigDecimal(3, history.getInputAmount());
            ps.setString(4, history.getOutputCurrency());
            ps.setBigDecimal(5, history.getOutputAmount());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().toString();
    }

    @Override
    public List<CurrencyConversionHistory> getAllCurrencyHistory() {
        String selectQuery = "SELECT * FROM history";
        return jdbcTemplate.query(selectQuery, new CurrencyHistoryMapper());
    }


    @Override
    public List<CurrencyConversionHistory> getCurrencyHistoryByInputCurrency(String inputCurrency) {
        String selectQuery = "SELECT * FROM history WHERE input_currency = ?";

        return jdbcTemplate.query(selectQuery, new CurrencyHistoryMapper(), inputCurrency);
    }

    public List<CurrencyConversionHistory> getAllCurrencyHistoryByDate(LocalDate date) {
        String selectQuery = "SELECT * FROM history WHERE conversion_date = ?";

        return jdbcTemplate.query(selectQuery, new CurrencyHistoryMapper(), date);
    }

    @Override
    public boolean isEmptyHistory() {
        String countQuery = "select COUNT(*) from history";
        Integer count = jdbcTemplate.queryForObject(countQuery, Integer.class);
        return count != null && count == 0;
    }

    @Override
    public void clearCurrencyConversionHistory() {
        String selectQuery = "DELETE FROM history";

        jdbcTemplate.update(selectQuery);
    }
}

