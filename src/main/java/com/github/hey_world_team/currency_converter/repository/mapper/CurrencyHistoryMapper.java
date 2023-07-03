package com.github.hey_world_team.currency_converter.repository.mapper;

import com.github.hey_world_team.currency_converter.model.CurrencyConversionHistory;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.RowMapper;


import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CurrencyHistoryMapper implements RowMapper<CurrencyConversionHistory> {


        @Override
        public CurrencyConversionHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CurrencyConversionHistory(
            rs.getLong("id"),
            rs.getDate("conversion_date"),
            rs.getString("input_currency"),
            rs.getBigDecimal("input_amount"),
            rs.getString("output_currency"),
            rs.getBigDecimal("output_amount"));
        }
    }

