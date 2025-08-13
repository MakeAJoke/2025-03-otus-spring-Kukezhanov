package ru.otus.hw.services.caches;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.mongo.MongoAuthor;
import ru.otus.hw.models.mongo.temp.MigrationMapping;
import ru.otus.hw.repository.AuthorRepository;
import ru.otus.hw.repository.MigrationMappingRepository;

import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Service
public class AuthorCacheService implements CacheService<MongoAuthor> {

    private static final String COLLECTION_NAME = "authors";

    private final MigrationMappingRepository migrationMappingRepository;

    private final AuthorRepository authorRepository;

    private final ConcurrentHashMap<Long, MongoAuthor> cache = new ConcurrentHashMap<>();

    @Override
    public MongoAuthor get(long key) {
        MongoAuthor mongoAuthor = null;
        mongoAuthor = cache.get(key);
        if (mongoAuthor == null) {
            mongoAuthor = migrationMappingRepository.findByEntityAndLegacyId(COLLECTION_NAME, key)
                    .flatMap(m -> authorRepository.findById(m.getMongoId()))
                    .orElse(null);
            if (mongoAuthor != null) {
                cache.put(key, mongoAuthor);
            }
        }
        return mongoAuthor;
    }

    @Override
    public void put(long key, MongoAuthor value) {
        cache.put(key, value);
        MigrationMapping migrationMapping =
                new MigrationMapping(null, COLLECTION_NAME, value.getLegacyId(), value.getId());
        migrationMappingRepository.save(migrationMapping);
    }
}
