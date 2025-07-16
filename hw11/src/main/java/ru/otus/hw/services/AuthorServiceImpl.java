package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.repositories.AuthorRepository;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorConverter authorConverter;

    private final TransactionalOperator transactionalOperator;

    @Override
    public Flux<AuthorDto> findAll() {
        return authorRepository
                .findAll()
                .map(authorConverter::authorToDto)
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<AuthorDto> save(Mono<AuthorDto> authorDtoMono) {
        return authorDtoMono
                .map(authorDto -> new Author(authorDto.fullName()))
                .flatMap(authorRepository::save)
                .map(authorConverter::authorToDto)
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<AuthorDto> update(Mono<AuthorDto> authorDtoMono) {
        return authorDtoMono.flatMap(authorDto ->
                authorRepository.findById(authorDto.id())
                        .switchIfEmpty(
                                Mono.error(
                                        new EntityNotFoundException(
                                                "Author with id %d not found".formatted(authorDto.id())
                                        )
                                )
                        ).map(author -> {
                            author.setFullName(authorDto.fullName());
                            return author;
                        })
                        .flatMap(author -> authorRepository.save(author)
                                .map(authorConverter::authorToDto)
                        )
        ).as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Void> deleteById(long id) {
        return authorRepository.deleteById(id).then()
                .as(transactionalOperator::transactional);
    }
}