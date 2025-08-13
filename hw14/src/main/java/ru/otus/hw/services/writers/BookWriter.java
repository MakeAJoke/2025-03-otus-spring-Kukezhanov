package ru.otus.hw.services.writers;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.mongo.MongoBook;
import ru.otus.hw.models.mongo.MongoGenre;
import ru.otus.hw.repository.BookRepository;
import ru.otus.hw.repository.BookRepositoryCustom;
import ru.otus.hw.services.caches.BookCacheService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@StepScope
public class BookWriter implements ItemWriter<MongoBook> {

    private final BookRepository bookRepository;

    private final BookRepositoryCustom bookRepositoryCustom;

    private final BookCacheService bookCacheService;

    @Override
    public void write(Chunk<? extends MongoBook> chunk) throws Exception {
        chunk.getItems().forEach(book -> {
            if (bookCacheService.get(book.getLegacyId()) == null) {
                List<String> genresId = book.getGenres().stream().map(MongoGenre::getId).toList();
                Optional<MongoBook> bookFromDb =
                        bookRepositoryCustom.findByTitleAuthorAndAllGenres(
                                book.getTitle(),
                                book.getAuthor().getId(),
                                genresId);
                if (bookFromDb.isEmpty()) {
                    MongoBook savedBook = bookRepository.save(book);
                    bookCacheService.put(book.getLegacyId(), savedBook.getId());
                }
            }
        });
    }
}
