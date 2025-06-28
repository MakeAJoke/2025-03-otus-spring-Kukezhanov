package ru.otus.hw.migrations.changeunits;

import com.mongodb.client.MongoCollection;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;

@ChangeUnit(id = "author-migration", order = "001", author = "mkukezhanov")
public class AuthorCollectionChangeUnit {

    private static final String COLLECTION_NAME = "authors";

    private final MongoTemplate mongoTemplate;

    public AuthorCollectionChangeUnit(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void createAuthors() {
        mongoTemplate.createCollection(COLLECTION_NAME);
        MongoCollection<Document> authors = mongoTemplate.getCollection(COLLECTION_NAME);

        Author author = new Author("Author_1");
        Author author2 = new Author("Author_2");
        Author author3 = new Author("Author_3");

        mongoTemplate.insert(author, COLLECTION_NAME);
        mongoTemplate.insert(author2, COLLECTION_NAME);
        mongoTemplate.insert(author3, COLLECTION_NAME);
    }

    @RollbackExecution
    public void rollback() {

    }
}
