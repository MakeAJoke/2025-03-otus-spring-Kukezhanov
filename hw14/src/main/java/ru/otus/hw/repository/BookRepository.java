package ru.otus.hw.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.MongoBook;

public interface BookRepository extends MongoRepository<MongoBook, String> {
}
