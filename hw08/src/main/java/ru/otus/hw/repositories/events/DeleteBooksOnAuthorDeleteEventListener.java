package ru.otus.hw.repositories.events;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.BookRepository;

@RequiredArgsConstructor
@Component
public class DeleteBooksOnAuthorDeleteEventListener extends AbstractMongoEventListener<Author> {

    private final BookRepository bookRepository;

    private final MongoConverter mongoConverter;

    @Override
    public void onAfterDelete(AfterDeleteEvent<Author> event) {
        Author author = mongoConverter.read(Author.class, event.getSource());
        bookRepository.deleteAllByAuthorId(author.getId());
    }
}
