package ru.otus.hw.services;

import org.springframework.data.jpa.repository.EntityGraph;
import ru.otus.hw.models.dto.BookDto;

import java.util.List;
import java.util.Optional;

public interface BookService {

    @EntityGraph("Book.withGenres")
    Optional<BookDto> findById(long id);

    @EntityGraph("Book.withGenres")
    List<BookDto> findAll();

    BookDto create(BookDto bookDto);

    BookDto update(BookDto bookDto);

    void deleteById(long id);
}
