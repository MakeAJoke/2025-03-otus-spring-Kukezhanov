package ru.otus.hw.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.temp.MigrationMapping;

import java.util.Optional;

public interface MigrationMappingRepository extends MongoRepository<MigrationMapping, String> {

    Optional<MigrationMapping> findByEntityAndLegacyId(String entity, Long id);

}
