package com.github.hey_world_team.currency_converter.repository;

import com.github.hey_world_team.currency_converter.model.Currency;
import com.github.hey_world_team.currency_converter.repository.mapper.CurrencyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
public class CurrencyRepositoryImpl implements CurrencyRepository {

    private static final Logger log = LoggerFactory.getLogger(CurrencyRepositoryImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CurrencyRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String saveCurrency(Currency currency) {
        String insertQuery = "WITH inserted_currency AS (" +
                "    INSERT INTO currency (id, name, nominal) " +
                "    VALUES (?, ?, ?) " +
                "    RETURNING id" +
                ") " +
                "INSERT INTO value (currency_id, value, date) " +
                "VALUES ((SELECT id FROM inserted_currency), ?, ?)";

        int rowsAffected = jdbcTemplate.update(insertQuery,
                currency.getId(),
                currency.getName(),
                currency.getNominal(),
                currency.getValue(),
                LocalDate.now());

        if (rowsAffected > 0) {
            return currency.getId();
        } else {
            log.error("Failed to save currency with id: {}", currency.getId());
            throw new RuntimeException("Failed to save currency with id: " + currency.getId());
        }
    }

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
                preparedStatement.setBigDecimal(4, currency.getValue());
                preparedStatement.setDate(5, Date.valueOf(LocalDate.now()));
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

    @Override
    public Currency getCurrencyById(String id) {
        String insertQuery = "SELECT c.id, c.name, c.nominal, v.value " +
                "FROM currency c " +
                "JOIN value v ON c.id = v.currency_id " +
                "WHERE id = ?";
        Currency currency = jdbcTemplate.queryForObject(insertQuery, new CurrencyMapper(), id);
        if (currency != null) {
            log.info("currency with id {} found", id);
            return currency;
        } else {
            log.info("currency with id {} NOT found", id);
            return null;
        }
    }

    @Override
    public Currency updateCurrency(Currency currency) {
        String updateQuery = "update value set value = ?, date = ?  WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(updateQuery,
                currency.getValue(),
                LocalDate.now(),
                currency.getId());

        if (rowsAffected > 0) {
            return currency;
        } else {
            log.error("Failed to update currency with id: {}", currency.getId());
            throw new RuntimeException("Failed to update currency with id: " + currency.getId());
        }
    }

    @Override
    public int updateCurrencies(List<Currency> currencies) {
        String updateQuery = "update value set value = ?, date = ?  WHERE currency_id = ?";
        int rowsAffected = jdbcTemplate.batchUpdate(updateQuery, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Currency currency = currencies.get(i);
                        ps.setBigDecimal(1, currency.getValue());
                        ps.setDate(2, Date.valueOf(LocalDate.now()));
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

    @Override
    public List<Currency> getAllCurrency() {
        String selectQuery = "SELECT c.id, c.name, c.nominal, v.value " +
                "FROM currency c " +
                "JOIN value v ON c.id = v.currency_id";

        return new ArrayList<>(jdbcTemplate.query(selectQuery, new CurrencyMapper()));
    }

    @Override
    public List<String> getAllCurrenciesIds() {
        return new ArrayList<>(jdbcTemplate.query("select id from currency",
                (rs, rowNum) -> rs.getString("id")));
    }

    public boolean isEmpty() {
        String countQuery = "select COUNT(*) from currency";
        Integer count = jdbcTemplate.queryForObject(countQuery, Integer.class);
        return count != null && count == 0;
    }
}
