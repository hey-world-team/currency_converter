package com.github.hey_world_team.currency_converter.repository.mapper;

import com.github.hey_world_team.currency_converter.model.History;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.RowMapper;


import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * HistoryMapper implements the RowMapper<History> interface,
 * which is used to map SQL query result rows to a History object
 */
@Component
public class HistoryMapper implements RowMapper<History> {

    /**
     * This method creates a new History object,
     * using the values from the current row of the ResultSet
     *
     * @param rs     the ResultSet to be mapped from DB
     * @param rowNum the number of the current row
     * @return a History object mapped from the current row of the ResultSet
     * @throws SQLException if a SQLException is encountered while processing the ResultSet
     */
    @Override
    public History mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new History(
                rs.getInt("id"),
                rs.getDate("conversion_date").toLocalDate(),
                rs.getString("input_currency"),
                rs.getBigDecimal("input_amount"),
                rs.getString("output_currency"),
                rs.getBigDecimal("output_amount")
        );
    }
}