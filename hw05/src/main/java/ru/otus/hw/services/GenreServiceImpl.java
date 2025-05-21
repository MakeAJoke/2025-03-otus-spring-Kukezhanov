package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Genre insert(String name) {
        return genreRepository.save(new Genre(0, name));
    }

    @Override
    public Genre update(long id, String name) {
        return genreRepository.save(new Genre(id, name));
    }

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
}
