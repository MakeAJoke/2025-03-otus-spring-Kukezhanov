package ru.otus.hw.services.processors;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.jpa.Genre;
import ru.otus.hw.models.mongo.MongoGenre;

@StepScope
@Service
public class GenreProcessor implements ItemProcessor<Genre, MongoGenre> {

    @Override
    public MongoGenre process(Genre genre) throws Exception {
        return new MongoGenre(null, genre.getName(), genre.getId());
    }
}
