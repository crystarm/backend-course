package ru.karandashov.repository;

import ru.karandashov.entity.GameEntity;
import ru.karandashov.exception.RepositoryException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class JdbcGameRepository implements GameRepository {

    private static final String INSERT_SQL = """
            INSERT INTO game (title, score)
            VALUES (?, ?)
            RETURNING id
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT id, title, score
            FROM game
            WHERE id = ?
            """;
    private static final String FIND_BY_TITLE_SQL = """
            SELECT id, title, score
            FROM game
            WHERE title = ?
            ORDER BY id
            LIMIT 1
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id, title, score
            FROM game
            ORDER BY id
            """;
    private static final String UPDATE_SQL = """
            UPDATE game
            SET title = ?, score = ?
            WHERE id = ?
            """;
    private static final String DELETE_SQL = """
            DELETE FROM game
            WHERE id = ?
            """;

    private final DataSource dataSource;

    public JdbcGameRepository(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource не может быть null");
    }

    @Override
    public int save(GameEntity entity) {
        Objects.requireNonNull(entity, "entity не может быть null");
        if (entity.getId() != null) {
            throw new IllegalArgumentException("У новой игры id должен быть null");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setString(1, entity.getTitle());
            statement.setDouble(2, entity.getScore());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new RepositoryException("База данных не вернула id созданной игры", null);
                }
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            throw new RepositoryException("Не удалось сохранить игру", e);
        }
    }

    @Override
    public GameEntity findById(int id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? map(resultSet) : null;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Не удалось найти игру с id=" + id, e);
        }
    }

    @Override
    public GameEntity findByField(String title) {
        Objects.requireNonNull(title, "title не может быть null");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_TITLE_SQL)) {
            statement.setString(1, title);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? map(resultSet) : null;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Не удалось найти игру по названию", e);
        }
    }

    @Override
    public List<GameEntity> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            List<GameEntity> games = new ArrayList<>();
            while (resultSet.next()) {
                games.add(map(resultSet));
            }
            return games;
        } catch (SQLException e) {
            throw new RepositoryException("Не удалось получить список игр", e);
        }
    }

    @Override
    public boolean update(GameEntity entity) {
        Objects.requireNonNull(entity, "entity не может быть null");
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Для обновления игры необходимо указать id");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, entity.getTitle());
            statement.setDouble(2, entity.getScore());
            statement.setInt(3, entity.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Не удалось обновить игру с id=" + entity.getId(), e);
        }
    }

    @Override
    public void deleteById(int id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Не удалось удалить игру с id=" + id, e);
        }
    }

    private static GameEntity map(ResultSet resultSet) throws SQLException {
        return new GameEntity(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getDouble("score")
        );
    }
}
