package com.karandashov.repository;

import com.karandashov.dto.NewsEvent;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RegisterRowMapper(NewsEventMapper.class)
public interface NewsEventDao {

    @SqlQuery("SELECT id, title, message FROM news_table WHERE id = :id")
    Optional<NewsEvent> getById(@Bind("id") UUID id);

    @SqlQuery("SELECT id, title, message FROM news_table ORDER BY title, id")
    List<NewsEvent> getAll();

    @SqlUpdate("INSERT INTO news_table (id, title, message) VALUES (:id, :title, :message)")
    void insert(@Bind("id") UUID id,
                @Bind("title") String title,
                @Bind("message") String message);
}
