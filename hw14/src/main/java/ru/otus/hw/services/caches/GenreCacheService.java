package ru.otus.hw.services.caches;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.mongo.MongoGenre;
import ru.otus.hw.models.mongo.temp.MigrationMapping;
import ru.otus.hw.repository.GenreRepository;
import ru.otus.hw.repository.MigrationMappingRepository;

import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Service
public class GenreCacheService implements CacheService<MongoGenre> {

    private static final String COLLECTION_NAME = "genres";

    private final MigrationMappingRepository migrationMappingRepository;

    private final GenreRepository genreRepository;

    private final ConcurrentHashMap<Long, MongoGenre> cache = new ConcurrentHashMap<>();

    @Override
    public MongoGenre get(long key) {
        MongoGenre mongoGenre = null;
        mongoGenre = cache.get(key);
        if (mongoGenre == null) {
            mongoGenre = migrationMappingRepository.findByEntityAndLegacyId(COLLECTION_NAME, key)
                    .flatMap(m -> genreRepository.findById(m.getMongoId()))
                    .orElse(null);
            if (mongoGenre != null) {
                cache.put(key, mongoGenre);
            }
        }
        return mongoGenre;
    }

    @Override
    public void put(long key, MongoGenre value) {
        cache.put(key, value);
        MigrationMapping migrationMapping =
                new MigrationMapping(null, COLLECTION_NAME, value.getLegacyId(), value.getId());
        migrationMappingRepository.save(migrationMapping);
    }
}
