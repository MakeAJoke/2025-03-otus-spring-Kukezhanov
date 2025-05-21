package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations jdbcTemplate;

    @Override
    public Optional<Book> findById(long id) {
        return Optional.ofNullable(
                jdbcTemplate.query("""
                                SELECT b.id as bId, b.title as bTitle,
                                    g.id as gId, g.name as gName,
                                    a.id as aId, a.full_name as aFullName
                                FROM books b
                                INNER JOIN books_genres bg ON bg.book_id = b.id
                                INNER JOIN genres g ON g.id = bg.genre_id
                                INNER JOIN authors a ON a.id = b.author_id
                                WHERE b.id = :id
                                """, Map.of("id", id),
                        new BookResultSetExtractor()));
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        Optional<Book> byIdOptional = findById(id);
        if (byIdOptional.isPresent()) {
            Book book = byIdOptional.get();
            removeGenresRelationsFor(book);
            jdbcTemplate.update("DELETE FROM books WHERE id = :id", Map.of("id", id));
        }
    }

    private List<Book> getAllBooksWithoutGenres() {
        return jdbcTemplate.query("""
                SELECT b.id as bId, b.title as bTitle, a.id as aId, a.full_name as aFullName
                FROM books b
                INNER JOIN authors a ON a.id = b.author_id""",
                new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return jdbcTemplate.query("SELECT book_id, genre_id FROM books_genres", new BookGenreRelationRawMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        Map<Long, Set<Long>> bookIdToGenreId = relations.stream()
                .collect(Collectors.groupingBy(BookGenreRelation::bookId,
                        Collectors.mapping(BookGenreRelation::genreId, Collectors.toSet())));
        booksWithoutGenres.forEach(book -> {
            Set<Long> bookGenresId = bookIdToGenreId.get(book.getId());
            List<Genre> bookGenres = genres.stream().filter(genre -> bookGenresId.contains(genre.getId())).toList();
            book.getGenres().addAll(bookGenres);
        });
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("authorId", book.getAuthor().getId());

        jdbcTemplate.update("INSERT INTO books(title, author_id) VALUES(:title, :authorId)",
                params, keyHolder, new String[]{"id"});

        Long id = keyHolder.getKeyAs(Long.class);
        if (id == null) {
            throw new IllegalStateException("Failed to retrieve generated key for book insert");
        }
        book.setId(id);
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        int updateResult = jdbcTemplate.update("UPDATE books SET title = :title, author_id = :authorId WHERE id = :id",
                Map.of("title", book.getTitle(), "authorId", book.getAuthor().getId(), "id", book.getId()));
        if (updateResult == 0) {
            throw new EntityNotFoundException(String.format("book with id %d not found", book.getId()));
        }
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        List<BookGenreRelation> list = book.getGenres().stream().map(genre ->
                new BookGenreRelation(book.getId(), genre.getId())).toList();
        jdbcTemplate.batchUpdate("INSERT INTO books_genres(book_id, genre_id) VALUES(:bookId, :genreId)",
                SqlParameterSourceUtils.createBatch(list));
    }

    private void removeGenresRelationsFor(Book book) {
        jdbcTemplate.update("DELETE FROM books_genres WHERE book_id = :bookId", Map.of("bookId", book.getId()));
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Author author = new Author(rs.getLong("aId"), rs.getString("aFullName"));
            return new Book(rs.getLong("bId"), rs.getString("bTitle"), author, new ArrayList<>());
        }
    }

    private static class BookGenreRelationRawMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BookGenreRelation(rs.getLong(1), rs.getLong(2));
        }
    }

    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            Book book = null;
            while (rs.next()) {
                if (book == null) {
                    book = new Book(rs.getLong("bId"),
                            rs.getString("bTitle"),
                            new Author(rs.getLong("aId"), rs.getString("aFullName")),
                            new ArrayList<>()
                    );
                }
                book.getGenres().add(
                        new Genre(rs.getLong("gId"), rs.getString("gName"))
                );
            }
            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
