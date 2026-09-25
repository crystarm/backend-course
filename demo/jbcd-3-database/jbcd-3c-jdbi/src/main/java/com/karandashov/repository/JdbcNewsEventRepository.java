package com.karandashov.repository;

import com.karandashov.dto.NewsEvent;
import com.karandashov.exception.RepositoryException;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JdbcNewsEventRepository implements NewsEventRepository {

    private final NewsEventDao dao;

    public JdbcNewsEventRepository(DataSource dataSource) {
        Jdbi jdbi = Jdbi.create(dataSource);
        jdbi.installPlugin(new SqlObjectPlugin());
        this.dao = jdbi.onDemand(NewsEventDao.class);
    }

    @Override
    public Optional<NewsEvent> getById(UUID id) {
        try {
            return dao.getById(id);
        } catch (Exception e) {
            throw new RepositoryException("Failed to fetch NewsEvent by id", e);
        }
    }

    @Override
    public List<NewsEvent> getAll() {
        try {
            return dao.getAll();
        } catch (Exception e) {
            throw new RepositoryException("Failed to fetch all NewsEvents", e);
        }
    }

    @Override
    public void save(NewsEvent newsEvent) {
        try {
            dao.insert(newsEvent.getId(), newsEvent.getTitle(), newsEvent.getMessage());
        } catch (Exception e) {
            throw new RepositoryException("Failed to save NewsEvent", e);
        }
    }
}
