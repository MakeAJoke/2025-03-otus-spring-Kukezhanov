package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.dto.CommentDto;

public interface CommentService {

    Mono<CommentDto> findById(long id);

    Flux<CommentDto> findAllByBookId(long id);

    Mono<CommentDto> save(long bookId, String text);

    Mono<CommentDto> update(long id, String text);

    Mono<Void> delete(long id);
}
