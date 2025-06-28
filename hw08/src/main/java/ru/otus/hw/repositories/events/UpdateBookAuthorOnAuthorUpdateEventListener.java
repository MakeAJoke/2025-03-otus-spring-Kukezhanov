package ru.otus.hw.repositories.events;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;

@RequiredArgsConstructor
@Component
public class UpdateBookAuthorOnAuthorUpdateEventListener extends AbstractMongoEventListener<Author> {

    private final BookRepository bookRepository;

    @Override
    public void onAfterSave(AfterSaveEvent<Author> event) {
        Author author = event.getSource();
        List<Book> books = bookRepository.findAllByAuthorId(author.getId());
        for (Book book : books) {
            book.setAuthor(author);
            bookRepository.save(book);
        }
    }
}
