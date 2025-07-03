package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final GenreConverter genreConverter;

    private final BookRepository bookRepository;

    @Override
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream()
                .map(genreConverter::genreToDto)
                .toList();
    }

    @Override
    public GenreDto create(GenreDto genreDto) {
        Genre genre = new Genre(genreDto.name());
        return genreConverter.genreToDto(genreRepository.save(genre));
    }

    @Override
    public GenreDto update(GenreDto genreDto) {
        Genre genre = genreRepository.findById(genreDto.id()).orElseThrow(() ->
                new EntityNotFoundException("Genre with id %s not found".formatted(genreDto.id())));
        genre.setName(genreDto.name());
        genreConverter.genreToDto(genreRepository.save(genre));

        List<Book> books = bookRepository.findAllByGenresId(genreDto.id());
        for (Book book : books) {
            book.getGenres().removeIf(bookGenre -> bookGenre.getId().equals(genre.getId()));
            book.getGenres().add(genre);
            bookRepository.save(book);
        }
        return genreDto;
    }

    @Override
    public void deleteById(String id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %s not found".formatted(id)));
        bookRepository.findAll().stream()
                .filter(book -> book.getGenres().contains(genre))
                .findAny()
                .ifPresent(book -> {
                    throw new IllegalArgumentException(
                            "Genre with id %s should not linked with any books".formatted(id)
                    );
                });
        genreRepository.deleteById(id);

    }
}
