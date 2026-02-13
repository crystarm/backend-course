package com.karandashov.repository;

import com.karandashov.dto.NewsEvent;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class NewsEventMapper implements RowMapper<NewsEvent> {
    @Override
    public NewsEvent map(ResultSet rs, StatementContext ctx) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String title = rs.getString("title");
        String message = rs.getString("message");
        return new NewsEvent(id, title, message);
    }
}
