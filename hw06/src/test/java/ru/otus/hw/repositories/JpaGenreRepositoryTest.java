package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaGenreRepository.class)
class JpaGenreRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private JpaGenreRepository genreRepository;

    @Test
    void shouldReturnAllGenres() {
        var genre1 = new Genre(0, "TestGenre_1");
        var genre2 = new Genre(0, "TestGenre_2");
        testEntityManager.persist(genre1);
        testEntityManager.persist(genre2);
        testEntityManager.flush();
        testEntityManager.clear();

        List<Genre> actualGenres = genreRepository.findAll();
        assertThat(actualGenres)
                .hasSize(2)
                .contains(genre1, genre2);
    }

    @Test
    void shouldReturnGenreById() {
        var expectedGenre = new Genre(0, "TestGenre_1");
        testEntityManager.persist(expectedGenre);
        testEntityManager.flush();

        long genreId1 = expectedGenre.getId();

        testEntityManager.clear();

        var actualGenre1 = genreRepository.findById(genreId1);
        assertThat(actualGenre1).isPresent().get().isEqualTo(expectedGenre);
    }

    @Test
    void shouldInsertGenre() {
        var expectedGenre = new Genre(0, "TestGenre_1");
        genreRepository.save(expectedGenre);
        testEntityManager.flush();

        long id = expectedGenre.getId();
        testEntityManager.clear();

        Genre actualGenre = testEntityManager.find(Genre.class, id);
        assertThat(actualGenre).isNotNull().isEqualTo(expectedGenre);
    }

    @Test
    void shouldUpdateGenre() {
        var genre = new Genre(0, "TestGenre_1");
        testEntityManager.persist(genre);
        testEntityManager.flush();

        long genreId = genre.getId();
        testEntityManager.clear();


        Optional<Genre> genreOptional = genreRepository.findById(genreId);
        assertThat(genreOptional).isPresent();
        testEntityManager.clear();


        Genre actualGenre = genreOptional.get();
        actualGenre.setName("Test_genre_2");
        actualGenre = genreRepository.save(actualGenre);
        testEntityManager.flush();
        testEntityManager.clear();

        var expectedGenre = testEntityManager.find(Genre.class, genreId);
        assertThat(expectedGenre).extracting(Genre::getName).isNotEqualTo(genre.getName());
        assertThat(expectedGenre).extracting(Genre::getName).isEqualTo(actualGenre.getName());
    }

    @Test
    void shouldDeleteGenre() {
        var genre = new Genre(0, "TestGenre_1");
        testEntityManager.persist(genre);
        testEntityManager.flush();

        long genreId = genre.getId();
        testEntityManager.clear();

        genreRepository.deleteById(genreId);
        testEntityManager.flush();
        testEntityManager.clear();

        Genre deletedGenre = testEntityManager.find(Genre.class, genreId);
        assertThat(deletedGenre).isNull();
    }
}
