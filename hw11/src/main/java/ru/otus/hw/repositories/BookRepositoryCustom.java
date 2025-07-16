package ru.otus.hw.repositories;

import io.r2dbc.spi.Readable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class BookRepositoryCustom {

    private static final String ALL_BOOKS_SQL = """
                    SELECT
                        b.id as book_id,
                        b.title,
                        a.id as author_id,
                        a.full_name
                    FROM books b
                    INNER JOIN authors a ON b.author_id = a.id
            """;

    private static final String BOOK_BY_ID_SQL = """
                    SELECT
                        b.id as book_id,
                        b.title,
                        a.id as author_id,
                        a.full_name
                    FROM books b
                    INNER JOIN authors a ON b.author_id = a.id
                    WHERE b.id = :bId
            """;

    private static final String INSERT_BOOK_TO_GENRES_LINK_SQL = """
            INSERT INTO books_genres (book_id, genre_id) VALUES (:bookId, :genreId)
            """;

    private static final String DELETE_BOOK_TO_GENRES_LINK_SQL = """
            DELETE FROM books_genres WHERE book_id = :bookId
            """;

    private static final String IS_BOOK_EXISTS_WITH_GENRE_SQL = """
            SELECT EXISTS(
                SELECT b.id FROM books b
                INNER JOIN books_genres bg on bg.book_id = b.id
                WHERE bg.genre_id = :gId
            )
            """;

    private final BookRepository bookRepository;

    private final R2dbcEntityOperations entityOperations;

    public Flux<Book> findAll() {
        return entityOperations.getDatabaseClient()
                .sql(ALL_BOOKS_SQL)
                .flatMap(row -> row.map(this::mapper));
    }

    public Mono<Book> findById(long bookId) {
        return entityOperations.getDatabaseClient()
                .sql(BOOK_BY_ID_SQL)
                .bind("bId", bookId)
                .map(this::mapper).first();
    }

    public Mono<Book> insert(Book book) {
        return bookRepository.save(book).flatMap(savedBook -> {
            book.setId(savedBook.getId());

            return insertLinksToGenres(book.getId(), book.getGenres())
                    .thenReturn(book);
        });
    }

    public Mono<Book> update(Book book) {
        return bookRepository.save(book).flatMap(savedBook -> {
            book.setId(savedBook.getId());
            return deleteLinksToGenres(book.getId())
                    .then(insertLinksToGenres(book.getId(), book.getGenres()))
                    .thenReturn(book);
        });
    }

    public Mono<Boolean> existsByGenreId(Long genreId) {
        return entityOperations.getDatabaseClient().sql(IS_BOOK_EXISTS_WITH_GENRE_SQL)
                .bind("gId", genreId)
                .map(row -> true)
                .first()
                .defaultIfEmpty(false);
    }

    private Mono<Void> deleteLinksToGenres(Long bookId) {
        return entityOperations.getDatabaseClient()
                        .sql(DELETE_BOOK_TO_GENRES_LINK_SQL)
                        .bind("bookId", bookId).then();
    }

    private Mono<Void> insertLinksToGenres(Long bookId, List<Genre> genreList) {
        return Flux.fromIterable(genreList).flatMap(genre ->
                entityOperations.getDatabaseClient()
                        .sql(INSERT_BOOK_TO_GENRES_LINK_SQL)
                        .bind("bookId", bookId)
                        .bind("genreId", genre.getId()).then()
        ).then();
    }

    private Book mapper(Readable result) {
        var author = new Author(
                (Long) result.get("author_id"),
                (String) result.get("full_name")
        );

        return new Book(
                (Long) result.get("book_id"),
                (String) result.get("title"),
                (Long) result.get("author_id"),
                author,
                new ArrayList<>()
        );
    }
}
