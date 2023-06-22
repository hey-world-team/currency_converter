package com.github.hey_world_team.currency_converter.repository.mapper;

import com.github.hey_world_team.currency_converter.model.Currency;
import com.github.hey_world_team.currency_converter.model.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CurrencyMapper implements RowMapper<Currency> {

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
        ));
    }
}
