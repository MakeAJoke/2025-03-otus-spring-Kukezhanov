package ru.otus.hw.services.processors;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.jpa.Book;
import ru.otus.hw.models.mongo.MongoAuthor;
import ru.otus.hw.models.mongo.MongoBook;
import ru.otus.hw.models.mongo.MongoGenre;
import ru.otus.hw.services.caches.AuthorCacheService;
import ru.otus.hw.services.caches.GenreCacheService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@StepScope
@Service
public class BookProcessor implements ItemProcessor<Book, MongoBook> {

    private final AuthorCacheService authorCacheService;

    private final GenreCacheService genreCacheService;

    @Override
    public MongoBook process(Book book) throws Exception {
        MongoAuthor author = authorCacheService.get(book.getAuthor().getId());
        List<MongoGenre> genres = book.getGenres().stream().map(genre ->
                genreCacheService.get(genre.getId())
        ).toList();
        return new MongoBook(null, book.getTitle(), author, genres, new ArrayList<>(), book.getId());
    }
}
