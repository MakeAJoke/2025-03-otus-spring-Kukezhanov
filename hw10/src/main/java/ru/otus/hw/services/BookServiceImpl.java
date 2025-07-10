package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookConverter bookConverter;

    @Transactional(readOnly = true)
    @Override
    public Optional<BookDto> findById(long id) {
        return bookRepository.findById(id).map(bookConverter::bookToDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream().map(bookConverter::bookToDto).toList();
    }

    @Transactional
    @Override
    public BookDto create(BookDto bookDto) {
        Set<Long> genresIdSet = bookDto.genres().stream()
                .map(GenreDto::id)
                .collect(Collectors.toSet());
        return save(0, bookDto.title(), bookDto.author().id(), genresIdSet);
    }

    @Transactional
    @Override
    public BookDto update(BookDto bookDto) {
        Set<Long> genresIdSet = bookDto.genres().stream()
                .map(GenreDto::id)
                .collect(Collectors.toSet());
        return save(
                bookDto.id(),
                bookDto.title(),
                bookDto.author().id(),
                genresIdSet
        );
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    private BookDto save(long id, String title, long authorId, Set<Long> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genres = genreRepository.findByIdIsIn(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        Book book = null;
        if (id == 0) {
            book = new Book(id, title, author, genres, new ArrayList<>());
        } else {
            book = bookRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(id)));
            book.setTitle(title);
            book.setAuthor(author);
            book.setGenres(genres);
        }
        return bookConverter.bookToDto(bookRepository.save(book));
    }
}
