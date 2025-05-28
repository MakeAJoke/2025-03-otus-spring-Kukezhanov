package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcAuthorRepository implements AuthorRepository {

    private final NamedParameterJdbcOperations jdbcTemplate;

    @Override
    public List<Author> findAll() {
        return jdbcTemplate.query("SELECT id, full_name FROM authors", new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(long id) {
        return Optional.ofNullable(
                jdbcTemplate.queryForObject("SELECT id, full_name FROM authors WHERE id = :id",
                        Collections.singletonMap("id", id), new AuthorRowMapper()));
    }

    @Override
    public Author save(Author author) {
        if (author.getId() == 0) {
            return insert(author);
        }
        return update(author);
    }

    @Override
    public void deleteById(long id) {
        jdbcTemplate.update("DELETE FROM authors WHERE id = :id", Map.of("id", id));
    }

    private Author insert(Author author) {
        var keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update("INSERT INTO authors(full_name) VALUES(:fullName)",
                new MapSqlParameterSource("fullName", author.getFullName()), keyHolder, new String[]{"id"});
        author.setId(keyHolder.getKeyAs(Long.class));
        return author;
    }

    private Author update(Author author) {
        int updateResult = jdbcTemplate.update("UPDATE authors SET full_name = :fullName WHERE id = :id",
                Map.of("fullName", author.getFullName(), "id", author.getId()));
        if (updateResult == 0) {
            throw new EntityNotFoundException(String.format("author with id %d not found", author.getId()));
        }
        return author;
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong("id");
            String fullName = rs.getString("full_name");
            return new Author(id, fullName);
        }
    }
}
