package ru.otus.hw.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.MongoAuthor;

import java.util.Optional;

public interface AuthorRepository extends MongoRepository<MongoAuthor, String> {

    Optional<MongoAuthor> findByFullName(String fullName);

}
