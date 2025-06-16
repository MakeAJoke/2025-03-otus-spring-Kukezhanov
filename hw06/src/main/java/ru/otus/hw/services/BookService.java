package ru.otus.hw.services;

import org.springframework.data.jpa.repository.EntityGraph;
import ru.otus.hw.models.dto.BookDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {

    @EntityGraph("Book.withGenres")
    Optional<BookDto> findById(long id);

    @EntityGraph("Book.withGenres")
    List<BookDto> findAll();

    BookDto create(String title, long authorId, Set<Long> genresIds);

    BookDto update(BookDto bookDto);

    void deleteById(long id);
}
