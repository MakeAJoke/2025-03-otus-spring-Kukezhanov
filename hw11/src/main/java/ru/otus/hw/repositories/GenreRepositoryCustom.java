package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Repository
public class GenreRepositoryCustom {

    private static final String BOOK_GENRES_SQL = """
                SELECT
                    bg.book_id,
                    g.id AS genre_id,
                    g.name AS genre_name
                FROM books_genres bg
                JOIN genres g ON g.id = bg.genre_id
                WHERE bg.book_id IN (%s)
            """;

    private final R2dbcEntityOperations entityOperations;

    public Mono<Map<Long, Collection<Genre>>> findAllBookGenres(List<Long> bookIds) {
        String inClause = IntStream.range(0, bookIds.size())
                .mapToObj(i -> ":id" + i)
                .collect(Collectors.joining(", "));

        DatabaseClient.GenericExecuteSpec sql = entityOperations
                .getDatabaseClient()
                .sql(BOOK_GENRES_SQL.formatted(inClause));
        for (int i = 0; i < bookIds.size(); i++) {
            sql = sql.bind("id" + i, bookIds.get(i));
        }
        return sql.fetch()
                .all()
                .collectMultimap(row ->
                        (Long) row.get("book_id"),
                        this::mapper);
    }

    private Genre mapper(Map<String, Object> result) {
        return new Genre(
                (Long) result.get("genre_id"),
                (String) result.get("genre_name")
        );
    }

}
