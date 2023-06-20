package com.github.hey_world_team.currency_converter.repository;

import com.github.hey_world_team.currency_converter.model.Currency;
import com.github.hey_world_team.currency_converter.repository.mapper.CurrencyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
        String updateQuery = "update currency set name = ?, nominal = ?  WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(updateQuery,
                currency.getName(),
                currency.getNominal(),
                currency.getId());

        if (rowsAffected > 0) {
            return currency;
        } else {
            log.error("Failed to update currency with id: {}", currency.getId());
            throw new RuntimeException("Failed to update currency with id: " + currency.getId());
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
}
