package ru.otus.hw.services;

import ru.otus.hw.models.dto.BookDto;

import java.util.List;
import java.util.Optional;

public interface BookService {

    Optional<BookDto> findById(long id);

    List<BookDto> findAll();

    BookDto create(BookDto bookDto);

    BookDto update(BookDto bookDto);

    void deleteById(long id);
}
