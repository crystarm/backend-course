package com.karandashov.repository;

import com.karandashov.dto.NewsEvent;
import com.karandashov.exception.RepositoryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JdbcNewsEventRepository implements NewsEventRepository {

    private final ConnectionFactory connectionFactory;

    private final String schema;

    private final String table;

    public JdbcNewsEventRepository(ConnectionFactory connectionFactory, String schema, String table) {
        this.connectionFactory = connectionFactory;
        this.schema = '"' + schema + '"';
        this.table = this.schema + "." + '"' + table + '"';
        createTables();
    }

    @Override
    public Optional<NewsEvent> getById(UUID id) {
        String sql = "SELECT id, title, message FROM " + table + " WHERE id = ?";
        try (
                Connection conn = connectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RepositoryException("Failed to fetch NewsEvent by id", e);
        }
    }

    @Override
    public List<NewsEvent> getAll() {
        String sql = "SELECT id, title, message FROM " + table + " ORDER BY title, id";
        try (
                Connection conn = connectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            List<NewsEvent> result = new ArrayList<>(rs.getFetchSize());
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RepositoryException("Failed to fetch all NewsEvents", e);
        }
    }

    @Override
    public void save(NewsEvent newsEvent) {
        String sql = "INSERT INTO " + table + " (id, title, message) VALUES (?, ?, ?)";
        try (
                Connection conn = connectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, newsEvent.getId());
            ps.setString(2, newsEvent.getTitle());
            ps.setString(3, newsEvent.getMessage());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Failed to save NewsEvent", e);
        }
    }

    private void createTables() {
        String createSchema = "CREATE SCHEMA IF NOT EXISTS " + schema;
        String createTable = "CREATE TABLE IF NOT EXISTS " + table + """
                (
                id UUID PRIMARY KEY,
                title TEXT NOT NULL,
                message TEXT NOT NULL
                )
                """;
        try (Connection conn = connectionFactory.getConnection()) {
            try (Statement st = conn.createStatement()) {
                st.execute(createSchema);
                st.execute(createTable);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to create tables", e);
        }
    }

    private static NewsEvent map(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String title = rs.getString("title");
        String message = rs.getString("message");
        return new NewsEvent(id, title, message);
    }
}
