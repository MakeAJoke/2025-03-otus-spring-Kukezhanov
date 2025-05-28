package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class JdbcGenreRepository implements GenreRepository {

    private final NamedParameterJdbcOperations jdbcTemplate;

    @Override
    public Optional<Genre> findById(long id) {
        return jdbcTemplate.query("SELECT id, name FROM genres WHERE id = :id", Map.of("id", id),
                        new GnreRowMapper()).stream().findFirst();

    }

    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query("SELECT id, name FROM genres", new GnreRowMapper());
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        return jdbcTemplate.query("SELECT id, name FROM genres WHERE id IN (:idList)",
                Collections.singletonMap("idList", ids), new GnreRowMapper());
    }

    @Override
    public Genre save(Genre genre) {
        if (genre.getId() == 0) {
            return insert(genre);
        }
        return update(genre);
    }

    @Override
    public void deleteById(long id) {
        jdbcTemplate.update("DELETE FROM genres WHERE id = :id", Map.of("id", id));
    }

    private Genre insert(Genre genre) {
        var keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update("INSERT INTO genres(name) VALUES(:name)",
                new MapSqlParameterSource("name", genre.getName()), keyHolder, new String[]{"id"});
        genre.setId(keyHolder.getKeyAs(Long.class));
        return genre;
    }

    private Genre update(Genre genre) {
        int updateResult = jdbcTemplate.update("UPDATE genres SET name = :name WHERE id = :id",
                Map.of("name", genre.getName(), "id", genre.getId()));
        if (updateResult == 0) {
            throw new EntityNotFoundException(String.format("genre with id %d not found", genre.getId()));
        }
        return genre;
    }

    private static class GnreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong("id");
            String name = rs.getString("name");
            return new Genre(id, name);
        }
    }
}
