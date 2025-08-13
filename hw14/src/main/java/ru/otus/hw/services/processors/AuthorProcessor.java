package ru.otus.hw.services.processors;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.jpa.Author;
import ru.otus.hw.models.mongo.MongoAuthor;

@RequiredArgsConstructor
@StepScope
@Service
public class AuthorProcessor implements ItemProcessor<Author, MongoAuthor> {

    @Override
    public MongoAuthor process(Author author) throws Exception {
        return new MongoAuthor(null, author.getFullName(), author.getId());
    }
}
