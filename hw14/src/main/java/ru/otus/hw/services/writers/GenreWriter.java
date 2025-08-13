package ru.otus.hw.services.writers;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.mongo.MongoGenre;
import ru.otus.hw.repository.GenreRepository;
import ru.otus.hw.services.caches.GenreCacheService;

@RequiredArgsConstructor
@Service
@StepScope
public class GenreWriter implements ItemWriter<MongoGenre> {

    private final GenreRepository genreRepository;

    private final GenreCacheService genreCacheService;

    @Override
    public void write(Chunk<? extends MongoGenre> chunk) throws Exception {
        chunk.getItems().forEach(genre -> {
            if (genreCacheService.get(genre.getLegacyId()) == null) {
                if (genreRepository.findByName(genre.getName()).isEmpty()) {
                    MongoGenre savedGenre = genreRepository.save(genre);
                    genreCacheService.put(genre.getLegacyId(), savedGenre);
                }
            }
        });
    }
}
