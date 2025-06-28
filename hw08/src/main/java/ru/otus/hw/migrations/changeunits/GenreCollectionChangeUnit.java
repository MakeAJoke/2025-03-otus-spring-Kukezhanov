package ru.otus.hw.migrations.changeunits;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Genre;

@ChangeUnit(id = "genres-migration", order = "002", author = "mkukezhanov")
public class GenreCollectionChangeUnit {

    private static final String COLLECTION_NAME = "genres";

    private final MongoTemplate mongoTemplate;


    public GenreCollectionChangeUnit(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void createGenres() {
        mongoTemplate.createCollection(COLLECTION_NAME);

        Genre genre1 = new Genre("Genre_1");
        Genre genre2 = new Genre("Genre_2");
        Genre genre3 = new Genre("Genre_3");

        mongoTemplate.save(genre1, COLLECTION_NAME);
        mongoTemplate.save(genre2, COLLECTION_NAME);
        mongoTemplate.save(genre3, COLLECTION_NAME);
    }

    @RollbackExecution
    public void rollback() {

    }
}
