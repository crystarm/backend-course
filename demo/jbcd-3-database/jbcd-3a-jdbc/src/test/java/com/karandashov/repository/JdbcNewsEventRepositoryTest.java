package com.karandashov.repository;

import com.karandashov.dto.NewsEvent;
import com.karandashov.exception.RepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class JdbcNewsEventRepositoryTest {

    private ConnectionFactory connectionFactory;

    private Connection connection;

    private NewsEventRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        connectionFactory = mock(ConnectionFactory.class);
        connection = mock(Connection.class);
        Statement statement = mock(Statement.class);

        // Stubs for constructor createTables()
        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        // CREATE SCHEMA / CREATE TABLE
        when(statement.execute(anyString())).thenReturn(true);

        repository = new JdbcNewsEventRepository(connectionFactory, "someSchemaName", "someTableName");

        // Clear interactions so tests focus on method-specific calls
        clearInvocations(connectionFactory, connection, statement);
    }

    @Test
    void testGetById_found() throws Exception {
        UUID id = UUID.randomUUID();
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        // Result set returns one row
        when(rs.next()).thenReturn(true);
        when(rs.getObject("id")).thenReturn(id);
        when(rs.getString("title")).thenReturn("Title");
        when(rs.getString("message")).thenReturn("Message");

        Optional<NewsEvent> result = repository.getById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals("Title", result.get().getTitle());
        assertEquals("Message", result.get().getMessage());

        verify(connectionFactory, times(1)).getConnection();
        verify(connection, times(1)).prepareStatement(anyString());
        verify(ps, times(1)).setObject(eq(1), eq(id));
        verify(ps, times(1)).executeQuery();
        verify(rs, atLeastOnce()).next();
    }

    @Test
    void testGetById_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(false);

        Optional<NewsEvent> result = repository.getById(id);

        assertTrue(result.isEmpty());
        verify(ps).setObject(eq(1), eq(id));
        verify(ps).executeQuery();
    }

    @Test
    void testGetAll_success() throws Exception {
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        when(rs.next()).thenReturn(true, true, false);
        when(rs.getObject("id")).thenReturn(id1, id2);
        when(rs.getString("title")).thenReturn("A", "B");
        when(rs.getString("message")).thenReturn("M1", "M2");

        List<NewsEvent> all = repository.getAll();

        assertEquals(2, all.size());
        assertEquals(id1, all.getFirst().getId());
        assertEquals("A", all.get(0).getTitle());
        assertEquals("M1", all.get(0).getMessage());
        assertEquals(id2, all.get(1).getId());
        assertEquals("B", all.get(1).getTitle());
        assertEquals("M2", all.get(1).getMessage());

        verify(connectionFactory, times(1)).getConnection();
        verify(connection, times(1)).prepareStatement(anyString());
        verify(ps, times(1)).executeQuery();
    }

    @Test
    void testSave_success() throws Exception {
        PreparedStatement ps = mock(PreparedStatement.class);

        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        UUID id = UUID.randomUUID();
        NewsEvent event = new NewsEvent(id, "T", "M");

        assertDoesNotThrow(() -> repository.save(event));

        verify(ps).setObject(1, id);
        verify(ps).setString(2, "T");
        verify(ps).setString(3, "M");
        verify(ps).executeUpdate();
    }

    @Test
    void testGetById_sqlExceptionWrapped() throws Exception {
        UUID id = UUID.randomUUID();
        when(connectionFactory.getConnection()).thenThrow(new SQLException("boom"));
        RepositoryException ex = assertThrows(RepositoryException.class, () -> repository.getById(id));
        assertTrue(ex.getMessage().contains("Failed to fetch NewsEvent by id"));
    }

    @Test
    void testGetAll_sqlExceptionWrapped() throws Exception {
        when(connectionFactory.getConnection()).thenThrow(new SQLException("boom"));
        RepositoryException ex = assertThrows(RepositoryException.class, () -> repository.getAll());
        assertTrue(ex.getMessage().contains("Failed to fetch all NewsEvents"));
    }

    @Test
    void testSave_sqlExceptionWrapped() throws Exception {
        when(connectionFactory.getConnection()).thenThrow(new SQLException("boom"));
        RepositoryException ex = assertThrows(RepositoryException.class, () -> repository.save(new NewsEvent(UUID.randomUUID(), "T", "M")));
        assertTrue(ex.getMessage().contains("Failed to save NewsEvent"));
    }

    @Test
    void testConstructor_createTables_sqlExceptionWrapped() throws Exception {
        ConnectionFactory failingFactory = mock(ConnectionFactory.class);
        Connection failingConn = mock(Connection.class);
        when(failingFactory.getConnection()).thenReturn(failingConn);
        when(failingConn.createStatement()).thenThrow(new SQLException("boom"));

        RepositoryException ex = assertThrows(RepositoryException.class,
                () -> new JdbcNewsEventRepository(failingFactory, "schema", "table"));
        assertTrue(ex.getMessage().contains("Failed to create tables"));
    }
}
