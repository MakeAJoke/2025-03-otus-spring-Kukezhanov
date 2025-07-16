package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.dto.BookDto;

public interface BookService {

    Mono<BookDto> findById(long id);

    Flux<BookDto> findAll();

    Mono<BookDto> create(Mono<BookDto> bookDtoMono);

    Mono<BookDto> update(Mono<BookDto> bookDtoMono);

    Mono<Void> deleteById(long id);
}
