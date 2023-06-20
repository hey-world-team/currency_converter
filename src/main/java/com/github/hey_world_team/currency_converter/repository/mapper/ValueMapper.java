package com.github.hey_world_team.currency_converter.repository.mapper;

import com.github.hey_world_team.currency_converter.model.Value;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ValueMapper implements RowMapper<Value> {
    @Override
    public Value mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Value(
                rs.getString("id"),
                rs.getBigDecimal("value"),
                rs.getDate("date").toLocalDate());
    }
}
