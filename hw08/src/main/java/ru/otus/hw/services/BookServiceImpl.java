package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    private final GenreRepository genreRepository;

    private final BookConverter bookConverter;


    @Override
    public Optional<BookDto> findById(String id) {
        return bookRepository.findById(id).map(bookConverter::bookToDto);
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream().map(bookConverter::bookToDto).toList();
    }

    @Override
    public BookDto create(BookDto bookDto) {
        Set<String> genresIdSet = bookDto.genres().stream()
                .map(GenreDto::id)
                .collect(Collectors.toSet());
        return save(null, bookDto.title(), bookDto.author().id(), genresIdSet);
    }

    @Override
    public BookDto update(BookDto bookDto) {
        Set<String> genresIdSet = bookDto.genres().stream()
                .map(GenreDto::id)
                .collect(Collectors.toSet());
        return save(
                bookDto.id(),
                bookDto.title(),
                bookDto.author().id(),
                genresIdSet
        );
    }

    @Override
    public void deleteById(String bookId) {
        bookRepository.deleteById(bookId);
    }

    private BookDto save(String id, String title, String authorId, Set<String> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(authorId)));
        var genres = genreRepository.findByIdIsIn(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        Book book = null;
        if (id == null) {
            book = new Book(id, title, author, genres, new ArrayList<>());
        } else {
            book = bookRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id)));
            book.setTitle(title);
            book.setAuthor(author);
            book.setGenres(genres);
        }
        return bookConverter.bookToDto(bookRepository.save(book));
    }
}
