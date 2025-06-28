package ru.otus.hw.migrations.changeunits;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.Collections;
import java.util.List;

@ChangeUnit(id = "books-migration", order = "003", author = "mkukezhanov")
public class BookCollectionChangeUnit {

    private static final String COLLECTION_NAME = "books";

    private final MongoTemplate mongoTemplate;

    public BookCollectionChangeUnit(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void createBooks() {
        mongoTemplate.createCollection(COLLECTION_NAME);

        Genre genre1 = mongoTemplate.findOne(Query.query(Criteria.where("name").is("Genre_1")), Genre.class);
        Genre genre2 = mongoTemplate.findOne(Query.query(Criteria.where("name").is("Genre_2")), Genre.class);
        Genre genre3 = mongoTemplate.findOne(Query.query(Criteria.where("name").is("Genre_3")), Genre.class);

        Author author1 = mongoTemplate.findOne(Query.query(Criteria.where("fullName").is("Author_1")), Author.class);
        Author author2 = mongoTemplate.findOne(Query.query(Criteria.where("fullName").is("Author_2")), Author.class);
        Author author3 = mongoTemplate.findOne(Query.query(Criteria.where("fullName").is("Author_3")), Author.class);

        Book book1 = new Book(null, "Book_1", author1, List.of(genre1, genre2), Collections.emptyList());
        Book book2 = new Book(null, "Book_2", author2, List.of(genre2, genre3), Collections.emptyList());
        Book book3 = new Book(null, "Book_3", author3, List.of(genre3, genre1), Collections.emptyList());

        mongoTemplate.save(book1, COLLECTION_NAME);
        mongoTemplate.save(book2, COLLECTION_NAME);
        mongoTemplate.save(book3, COLLECTION_NAME);
    }

    @RollbackExecution
    public void rollback() {

    }
}
