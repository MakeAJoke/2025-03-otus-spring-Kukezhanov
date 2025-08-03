package ru.otus.hw.services.caches;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.mongo.temp.MigrationMapping;
import ru.otus.hw.repository.MigrationMappingRepository;

import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Service
public class BookCacheService implements CacheService<String> {

    private static final String COLLECTION_NAME = "books";

    private final MigrationMappingRepository migrationMappingRepository;

    private final ConcurrentHashMap<Long, String> cache = new ConcurrentHashMap<>();

    @Override
    public String get(long key) {
        String mongoBookId = null;
        mongoBookId = cache.get(key);
        if (mongoBookId == null) {
            mongoBookId = migrationMappingRepository.findByEntityAndLegacyId(COLLECTION_NAME, key)
                    .map(MigrationMapping::getMongoId)
                    .orElse(null);
            if (mongoBookId != null) {
                cache.put(key, mongoBookId);
            }
        }
        return mongoBookId;
    }

    @Override
    public void put(long key, String value) {
        cache.put(key, value);
        MigrationMapping migrationMapping =
                new MigrationMapping(null, COLLECTION_NAME, key, value);
        migrationMappingRepository.save(migrationMapping);
    }
}
