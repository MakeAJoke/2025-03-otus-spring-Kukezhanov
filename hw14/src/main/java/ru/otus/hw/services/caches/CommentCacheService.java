package ru.otus.hw.services.caches;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.mongo.temp.MigrationMapping;
import ru.otus.hw.repository.MigrationMappingRepository;

@AllArgsConstructor
@Service
public class CommentCacheService implements CacheService<String> {

    private static final String COLLECTION_NAME = "comments";

    private final MigrationMappingRepository migrationMappingRepository;

    @Override
    public String get(long key) {
        String mongoCommentId = migrationMappingRepository.findByEntityAndLegacyId(COLLECTION_NAME, key)
                .map(MigrationMapping::getMongoId)
                .orElse(null);
        return mongoCommentId;
    }

    @Override
    public void put(long key, String value) {
        MigrationMapping migrationMapping =
                new MigrationMapping(null, COLLECTION_NAME, key, value);
        migrationMappingRepository.save(migrationMapping);
    }
}
