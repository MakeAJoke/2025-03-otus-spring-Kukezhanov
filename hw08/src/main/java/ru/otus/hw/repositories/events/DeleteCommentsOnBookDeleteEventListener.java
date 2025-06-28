package ru.otus.hw.repositories.events;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.CommentRepository;

@RequiredArgsConstructor
@Component
public class DeleteCommentsOnBookDeleteEventListener extends AbstractMongoEventListener<Book> {

    private final CommentRepository commentRepository;

    private final MongoConverter mongoConverter;

    @Override
    public void onAfterDelete(AfterDeleteEvent<Book> event) {
        Book book = mongoConverter.read(Book.class, event.getSource());
        commentRepository.deleteAllByBookId(book.getId());
    }
}
