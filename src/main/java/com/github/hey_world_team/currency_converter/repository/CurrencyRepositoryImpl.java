package com.github.hey_world_team.currency_converter.repository;

import com.github.hey_world_team.currency_converter.model.Currency;
import com.github.hey_world_team.currency_converter.model.History;
import com.github.hey_world_team.currency_converter.repository.mapper.CurrencyMapper;
import com.github.hey_world_team.currency_converter.repository.mapper.HistoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * CurrencyRepositoryImpl provides implementation of methods for working with the database, functionality for working
 * with currency data and conversion history, and also provides interaction with the database through JdbcTemplate
 */
@Repository
public class CurrencyRepositoryImpl implements CurrencyRepository, HistoryRepository {

    private static final Logger log = LoggerFactory.getLogger(CurrencyRepositoryImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CurrencyRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * This method saves Currency object in the data store. It executes SQL queries to insert the currency and its value
     * into the currency and value tables
     *
     * @param currency the Currency object to be saved
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
     * @param currencies the list of Currency objects to be saved
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
     * This method returns a Currency object with the specified ID found in the data store It executes a SQL query to
     * retrieve currency data by currency ID
     *
     * @param id the ID of the currency to retrieve
     * @return the Currency object with the specified ID, or null if not found
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
     * This method updates the Currency object in the data store It executes an SQL query to update the value and date
     * of the currency in the value table by its ID
     *
     * @param currency the Currency object to update
     * @return the updated Currency object, or throws a RuntimeException if the operation fails
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
     * @param currencies the list of Currency objects to update
     * @return the number of updated records, or throws a RuntimeException if the operation fails
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
     * @param startDate the start date of the period
     * @param endDate   the end date of the period
     * @param idFirst   the ID of the first currency
     * @param idSecond  the ID of the second currency
     * @return a list of Currency objects within the specified period for the specified currency identifiers
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
     * @param date the date for which to retrieve currencies
     * @return list of Currency objects for the given date
     */
    @Override
    public List<Currency> getAllCurrency(LocalDate date) {
        String selectQuery = "SELECT c.id, c.name, c.nominal, v.value, v.date " +
                             "FROM currency c " +
                             "JOIN value v ON c.id = v.currency_id where v.date = ? AND v.currency_id != 'RUB'";

        return new ArrayList<>(jdbcTemplate.query(selectQuery, new CurrencyMapper(), date));
    }

    /**
     * This method retrieves  a list of identifiers of all currencies
     *
     * @return a list of identifiers of all currencies
     */
    @Override
    public List<String> getAllCurrenciesIds() {
        return new ArrayList<>(jdbcTemplate.query("select id from currency",
                                                  (rs, rowNum) -> rs.getString("id")));
    }

    /**
     * This method checks repository is empty or not
     *
     * @return true if the repository is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        String countQuery = "select COUNT(*) from currency";
        Integer count = jdbcTemplate.queryForObject(countQuery, Integer.class);
        return count != null && count == 0;

    }

    /**
     * This method saves the currency conversion history to the database
     *
     * @param history the currency conversion history to be saved
     * @return a string representation of the generated key (ID) of the saved history entry
     */
    @Override
    public int saveHistory(History history) {
        String insertQuery =
            "INSERT INTO history (id, conversion_date, input_currency, input_amount, output_currency, output_amount) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        int rowsAffected = jdbcTemplate.update(insertQuery,
                                               history.getId(),
                                               history.getConversionDate(),
                                               history.getInputCurrency(),
                                               history.getInputAmount(),
                                               history.getOutputCurrency(),
                                               history.getOutputAmount());

        if (rowsAffected > 0) {
            return history.getId();
        } else {
            log.error("Failed to save history with id: {}", history.getId());
            throw new RuntimeException("Failed to save history with id: " + history.getId());
        }
    }

    /**
     * This method retrieves a list of all currency conversion history entries
     *
     * @return a list of all currency conversion history entries
     */
    @Override
    public List<History> getAllCurrencyHistory() {
        String selectQuery = "SELECT * FROM history";
        return jdbcTemplate.query(selectQuery, new HistoryMapper());
    }

    /**
     * This method retrieves a list of currency conversion history entries based on the input currency
     *
     * @param inputCurrency the input currency from DB
     * @return a list of currency conversion history entries based on the input currency
     */
    @Override
    public List<History> getCurrencyHistoryByInputCurrency(String inputCurrency) {
        String selectQuery = "SELECT * FROM history WHERE input_currency = ?";

        return jdbcTemplate.query(selectQuery, new HistoryMapper(), inputCurrency);
    }

    /**
     * This method retrieves a list of currency conversion history entries for a given date
     *
     * @param date the date for which to retrieve currency conversion history entries
     * @return a list of currency conversion history entries for the given date
     */
    @Override
    public List<History> getAllCurrencyHistoryByDate(LocalDate date) {
        String selectQuery = "SELECT * FROM history WHERE conversion_date = ?";

        return jdbcTemplate.query(selectQuery, new HistoryMapper(), date);
    }

    /**
     * This method checks if the currency conversion history is empty
     *
     * @return true if the currency conversion history is empty, false otherwise
     */
    @Override
    public boolean isEmptyHistory() {
        String countQuery = "select COUNT(*) from history";
        Integer count = jdbcTemplate.queryForObject(countQuery, Integer.class);
        return count != null && count == 0;
    }

    /**
     * This method clears  the currency conversion history from the database
     */
    @Override
    public void clearHistory() {
        String selectQuery = "DELETE FROM history";

        jdbcTemplate.update(selectQuery);
    }
}