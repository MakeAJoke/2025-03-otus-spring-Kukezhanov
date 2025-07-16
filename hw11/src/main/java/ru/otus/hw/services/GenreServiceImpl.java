package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.repositories.BookRepositoryCustom;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final BookRepositoryCustom bookRepositoryCustom;

    private final GenreConverter genreConverter;

    @Override
    public Flux<GenreDto> findAll() {
        return genreRepository.findAll().map(genreConverter::genreToDto);
    }

    @Override
    public Mono<GenreDto> save(String name) {
        Genre genre = new Genre(0, name);
        return genreRepository.save(genre).map(genreConverter::genreToDto);
    }

    @Override
    public Mono<GenreDto> update(long id, String name) {
        Genre genre = new Genre(id, name);
        return genreRepository.save(genre).map(genreConverter::genreToDto);
    }

    @Override
    public Mono<Void> deleteById(long id) {
        return genreRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Genre with id %d not found".formatted(id))))
                .flatMap(genre -> bookRepositoryCustom.existsByGenreId(id)
                        .flatMap(result -> {
                            if (result) {
                                return Mono.error(
                                        new IllegalArgumentException(
                                                "Genre with id %d should not linked with any books".formatted(id)
                                        )
                                );
                            } else {
                                return genreRepository.deleteById(id).then();
                            }
                        })
                );
    }
}
