package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RequiredArgsConstructor
@ShellComponent
public class BookCommands {

    private final BookService bookService;

    private final BookConverter bookConverter;

    @ShellMethod(value = "Find all books", key = "ab")
    public String findAllBooks() {
        return bookService.findAll().stream()
                .map(bookConverter::bookDtoToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find book by id", key = "bbid")
    public String findBookById(String id) {
        return bookService.findById(id)
                .map(bookConverter::bookDtoToString)
                .orElse("Book with id %s not found".formatted(id));
    }

    // bins newBook 1 1,6
    @ShellMethod(value = "Insert book", key = "bins")
    public String insertBook(String title, String authorId, Set<String> genresIds) {
        List<GenreDto> genreDtoList = genresIds.stream().map(genreId ->
                new GenreDto(genreId, null)).toList();
        BookDto bookDto = new BookDto(null, title, new AuthorDto(authorId, null), genreDtoList);
        bookDto = bookService.create(bookDto);
        return bookConverter.bookDtoToString(bookDto);
    }

    // bupd 4 editedBook 3 2,5
    @ShellMethod(value = "Update book", key = "bupd")
    public String updateBook(String id, String title, String authorId, Set<String> genresIds) {
        List<GenreDto> genreDtoList = genresIds.stream().map(genreId ->
                new GenreDto(genreId, null)).toList();
        BookDto bookDto = new BookDto(null, title, new AuthorDto(authorId, null), genreDtoList);
        bookDto = bookService.update(bookDto);
        return bookConverter.bookDtoToString(bookDto);
    }

    // bdel 4
    @ShellMethod(value = "Delete book by id", key = "bdel")
    public void deleteBook(String id) {
        bookService.deleteById(id);
    }
}
