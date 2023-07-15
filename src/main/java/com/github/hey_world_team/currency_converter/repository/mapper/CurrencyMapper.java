package com.github.hey_world_team.currency_converter.repository.mapper;

import com.github.hey_world_team.currency_converter.model.Currency;
import com.github.hey_world_team.currency_converter.model.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * CurrencyMapper implements the RowMapper<Currency> interface,
 * which is used to map SQL query result rows to a Currency object
 */
@Component
public class CurrencyMapper implements RowMapper<Currency> {

    /**
     * This method creates a new Currency object,
     * using the values from the current row of the ResultSet
     *
     * @param rs     the ResultSet to be mapped from DB
     * @param rowNum the number of the current row
     * @return a Currency object mapped from the current row of the ResultSet
     * @throws SQLException if a SQLException is encountered while processing the ResultSet
     */
    @Override
    public Currency mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Currency(
                rs.getString("id"),
                rs.getString("name"),
                rs.getInt("nominal"),
                new Value(
                        rs.getBigDecimal("value"),
                        (rs.getDate("date") != null)
                                ? rs.getDate("date").toLocalDate()
                                : null
                )
        );
    }
}