package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.dto.CommentDto;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentConverter commentConverter;

    private final BookRepository bookRepository;

    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<CommentDto> findById(long id) {
        return commentRepository.findById(id).map(commentConverter::commentToDto);
    }

    @Override
    public Flux<CommentDto> findAllByBookId(long id) {
        return commentRepository.findAllByBookId(id).map(commentConverter::commentToDto);
    }

    @Override
    public Mono<CommentDto> save(long bookId, String text) {
        return bookRepository.findById(bookId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book with id %d not found".formatted(bookId))))
                .flatMap(book -> {
                    Comment comment = new Comment(0, text, bookId);
                    return commentRepository.save(comment);
                })
                .map(commentConverter::commentToDto)
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<CommentDto> update(long id, String text) {
        return commentRepository.findById(id).flatMap(comment -> {
            comment.setText(text);
            return commentRepository.save(comment).map(commentConverter::commentToDto);
        }).as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Void> delete(long id) {
        return commentRepository.deleteById(id).then();
    }
}
