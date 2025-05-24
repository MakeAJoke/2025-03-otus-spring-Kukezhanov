package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с жанрами")
@JdbcTest
@Import({JdbcGenreRepository.class, JdbcBookRepository.class})
public class JdbcGenreRepositoryTest {

    @Autowired
    private JdbcGenreRepository genreRepository;

    private List<Genre> dbGenres;

    @BeforeEach
    void setUp() {
        dbGenres = getDbGenres();
    }

    @DisplayName("должен загружать жанр по id")
    @ParameterizedTest
    @MethodSource("getDbGenres")
    void shouldReturnCorrectGenreById(Genre expectedGenre) {
        var actualGenre = genreRepository.findById(expectedGenre.getId());
        assertThat(actualGenre).isPresent()
                .get()
                .isEqualTo(expectedGenre);
    }

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenreList() {
        var actualGenres = genreRepository.findAll();
        var expectedGenres = dbGenres;

        assertThat(actualGenres).containsExactlyElementsOf(expectedGenres);
        actualGenres.forEach(System.out::println);
    }

    @DisplayName("должен загружать список жанров по списку id")
    @Test
    void shouldReturnCorrectGenresByIdList() {
        Set<Long> genreIdList = dbGenres.stream().map(Genre::getId).collect(Collectors.toSet());
        var expectedGenres = dbGenres;
        var actualGenres = genreRepository.findAllByIds(genreIdList);
        assertThat(actualGenres).isEqualTo(expectedGenres);
    }

    @DisplayName("должен сохранять новый жанр")
    @Test
    void shouldSaveNewGenre() {
        var expectedGenre = new Genre(0, "Genre_10500");
        var returnedGenre = genreRepository.save(expectedGenre);
        assertThat(returnedGenre).isNotNull()
                .matches(genre -> genre.getId() > 0).isEqualTo(expectedGenre);
    }

    @DisplayName("должен сохранять измененный жанр")
    @Test
    void shouldSaveUpdatedGenre() {
        var expectedGenre = new Genre(1L, "Genre_10500");

        assertThat(genreRepository.findById(expectedGenre.getId()))
                .isPresent()
                .get()
                .isNotEqualTo(expectedGenre);

        var returnedGenre = genreRepository.save(expectedGenre);
        assertThat(returnedGenre).isNotNull()
                .matches(genre -> genre.getId() > 0)
                .isEqualTo(expectedGenre);

        assertThat(genreRepository.findById(returnedGenre.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedGenre);
    }

    @DisplayName("должен удалять жанр по id")
    @Test
    void shouldDeleteGenreWithoutBook() {
        assertThat(genreRepository.findById(1L)).isPresent();
        genreRepository.deleteById(1L);
        assertThat(genreRepository.findById(1L)).isEmpty();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }
}
