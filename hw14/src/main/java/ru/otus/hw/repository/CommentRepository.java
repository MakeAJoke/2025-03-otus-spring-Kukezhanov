package ru.otus.hw.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.MongoComment;

import java.util.Optional;

public interface CommentRepository extends MongoRepository<MongoComment, String> {

    public Optional<MongoComment> findByTextAndMongoBookId(String text, String bookId);
}
