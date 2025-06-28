package ru.otus.hw.repositories.events;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;

@RequiredArgsConstructor
@Component
public class UpdateBookGenresOnGenreUpdateEventListener extends AbstractMongoEventListener<Genre> {

    private final BookRepository bookRepository;

    @Override
    public void onAfterSave(AfterSaveEvent<Genre> event) {
        Genre genre = event.getSource();
        List<Book> books = bookRepository.findAllByGenresId(genre.getId());
        for (Book book : books) {
            book.getGenres().removeIf(bookGenre -> bookGenre.getId().equals(genre.getId()));
            book.getGenres().add(genre);
            bookRepository.save(book);
        }
    }
}
