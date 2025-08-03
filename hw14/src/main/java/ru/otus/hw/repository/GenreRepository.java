package ru.otus.hw.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.MongoGenre;

import java.util.Optional;

public interface GenreRepository extends MongoRepository<MongoGenre, String> {

    Optional<MongoGenre> findByName(String name);
}
