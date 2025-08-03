package ru.otus.hw.services.writers;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.mongo.MongoAuthor;
import ru.otus.hw.repository.AuthorRepository;
import ru.otus.hw.services.caches.AuthorCacheService;

@RequiredArgsConstructor
@Service
@StepScope
public class AuthorWriter implements ItemWriter<MongoAuthor> {

    private final AuthorRepository authorRepository;

    private final AuthorCacheService authorCacheService;

    @Override
    public void write(Chunk<? extends MongoAuthor> chunk) throws Exception {
        chunk.getItems().forEach(author -> {
            if (authorCacheService.get(author.getLegacyId()) == null) {
                if (authorRepository.findByFullName(author.getFullName()).isEmpty()) {
                    MongoAuthor savedAuthor = authorRepository.save(author);
                    authorCacheService.put(author.getLegacyId(), savedAuthor);
                }
            }
        });
    }
}
