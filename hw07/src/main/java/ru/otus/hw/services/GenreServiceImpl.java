package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final GenreConverter genreConverter;

    @Transactional(readOnly = true)
    @Override
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream().map(genreConverter::genreToDto).toList();
    }

    @Transactional
    @Override
    public GenreDto save(String name) {
        return genreConverter.genreToDto(genreRepository.save(new Genre(0, name)));
    }

    @Transactional
    @Override
    public GenreDto update(long id, String name) {
        return genreConverter.genreToDto(genreRepository.save(new Genre(id, name)));
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(id)));
        bookRepository.findAll().stream()
                .filter(book -> book.getGenres().contains(genre.getId()))
                .findAny()
                .ifPresent(book -> {
                    throw new IllegalArgumentException(
                            "Genre with id %d should not linked with any books".formatted(id)
                    );
                });
        genreRepository.deleteById(id);
    }
//    @Override
//    public void deleteById(long id) {
//        Genre genre = findById(id).orElseThrow(() ->
//                new EntityNotFoundException("Book with id %d not found".formatted(id)));
//        entityManager.remove(genre);
//    }
}
