package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.dto.GenreDto;

public interface GenreService {
    Flux<GenreDto> findAll();

    Mono<GenreDto> save(String name);

    Mono<GenreDto> update(long id, String name);

    Mono<Void> deleteById(long id);
}
