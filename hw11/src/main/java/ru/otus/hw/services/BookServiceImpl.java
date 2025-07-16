package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.BookRepositoryCustom;
import ru.otus.hw.repositories.GenreRepositoryCustom;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookRepositoryCustom bookRepositoryCustom;

    private final GenreRepositoryCustom genreRepositoryCustom;

    private final BookConverter bookConverter;

    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<BookDto> findById(long id) {
        Mono<Book> bookMono = bookRepositoryCustom.findById(id);
        return bookMono.flatMap(book -> {
            Mono<Map<Long, Collection<Genre>>> booksToGenres = genreRepositoryCustom
                    .findAllBookGenres(List.of(book.getId()));

            return booksToGenres.map(bookIdToGenres -> {
                List<Genre> bookGenreDtos = (List<Genre>) bookIdToGenres.get(book.getId());
                book.setGenres(bookGenreDtos);
                return bookConverter.bookToDto(book);
            });
        });
    }

    @Override
    public Flux<BookDto> findAll() {
        Flux<Book> booksFlux = bookRepositoryCustom.findAll();
        return booksFlux.collectList().flatMapMany(books -> {
            List<Long> bookIds = books.stream()
                    .map(Book::getId)
                    .toList();

            Mono<Map<Long, Collection<Genre>>> booksToGenres = genreRepositoryCustom.findAllBookGenres(bookIds);
            return booksToGenres.flatMapMany(bookIdToGenres ->
                    Flux.fromIterable(books).map(book -> {
                        List<Genre> bookGenreDtos = (List<Genre>) bookIdToGenres.get(book.getId());
                        book.setGenres(bookGenreDtos);
                        return bookConverter.bookToDto(book);
                    })
            );
        });
    }

    @Override
    public Mono<BookDto> create(Mono<BookDto> bookDtoMono) {
        return bookDtoMono.map(bookDto -> {
            Author author = new Author(bookDto.author().id(), bookDto.author().fullName());
            List<Genre> genres = bookDto.genres().stream().map(genreDto ->
                    new Genre(genreDto.id(), genreDto.name())
            ).toList();
            return new Book(
                    bookDto.id(),
                    bookDto.title(),
                    bookDto.author().id(),
                    author,
                    genres
            );
        }).flatMap(bookRepositoryCustom::insert)
                .map(bookConverter::bookToDto)
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<BookDto> update(Mono<BookDto> bookDtoMono) {
        return bookDtoMono.flatMap(bookDto ->
            bookRepository.findById(bookDto.id()).switchIfEmpty(
                    Mono.error(
                            new EntityNotFoundException("Book with id %d not found".formatted(bookDto.id()))
                    )
            ).map(foundBook -> {
                Author author = new Author(bookDto.author().id(), bookDto.author().fullName());
                List<Genre> genres = bookDto.genres().stream().map(genreDto ->
                        new Genre(genreDto.id(), genreDto.name())
                ).toList();
                foundBook.setTitle(bookDto.title());
                foundBook.setGenres(genres);
                foundBook.setAuthor(author);
                return foundBook;
            }).flatMap(bookRepositoryCustom::update)
                    .map(bookConverter::bookToDto)
        ).as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Void> deleteById(long id) {
        return bookRepository.deleteById(id).then();
    }
}
