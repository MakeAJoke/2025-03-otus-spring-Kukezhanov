package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.shell.boot.ShellRunnerAutoConfiguration;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.dto.GenreDto;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
        ShellRunnerAutoConfiguration.class
})
class GenreServiceImplIntegrationTest {

    @Autowired
    private GenreService genreService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GenreConverter genreConverter;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(Author.class);
        mongoTemplate.dropCollection(Book.class);
        mongoTemplate.dropCollection(Genre.class);

        Author author1 = new Author("Author_1");
        Author author2 = new Author("Author_2");
        Author author3 = new Author("Author_3");

        author1 = mongoTemplate.insert(author1);
        author2 = mongoTemplate.insert(author2);
        author3 = mongoTemplate.insert(author3);

        Genre genre1 = new Genre("Genre_1");
        Genre genre2 = new Genre("Genre_2");

        genre1 = mongoTemplate.insert(genre1);
        genre2 = mongoTemplate.insert(genre2);

        Book book1 = new Book(null, "Book_1", author1, List.of(genre1, genre2), Collections.emptyList());
        Book book2 = new Book(null, "Book_2", author2, List.of(genre1, genre2), Collections.emptyList());
        Book book3 = new Book(null, "Book_3", author3, List.of(genre1, genre2), Collections.emptyList());

        mongoTemplate.insert(book1);
        mongoTemplate.insert(book2);
        mongoTemplate.insert(book3);
    }

    @Test
    void shouldReturnAllGenres() {
        List<GenreDto> genreDtos = genreService.findAll();
        assertThat(genreDtos.size()).isEqualTo(2);
        assertThat(genreDtos)
                .extracting(GenreDto::name)
                .contains("Genre_1", "Genre_2");
    }

    @Test
    void shouldCreateAndReturnGenre() {
        GenreDto genreDto = new GenreDto(null, "NEW_GENRE");
        GenreDto expectedGenreDto = genreService.create(genreDto);

        Genre actualGenre = mongoTemplate.findById(expectedGenreDto.id(), Genre.class);


        assertThat(actualGenre).isNotNull();
        assertThat(actualGenre).extracting(Genre::getName).isEqualTo(expectedGenreDto.name());
    }

    @Test
    void shouldUpdateGenre() {
        Genre genre = mongoTemplate.findOne(
                Query.query(Criteria.where("name").is("Genre_1")),
                Genre.class
        );

        assertThat(genre).isNotNull();

        genre.setName("UPDATED_GENRE_NAME");

        GenreDto genreDto = genreConverter.genreToDto(genre);

        GenreDto expectedGenreDto = genreService.update(genreDto);

        Genre actualGenre = mongoTemplate.findOne(
                Query.query(Criteria.where("name").is("Genre_1")),
                Genre.class
        );
        assertThat(actualGenre).isNull();

        actualGenre = mongoTemplate.findOne(
                Query.query(new Criteria().andOperator(
                                Criteria.where("id").is(expectedGenreDto.id()),
                                Criteria.where("name").is("UPDATED_GENRE_NAME")
                        )
                ),
                Genre.class
        );
        assertThat(actualGenre).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenUpdateInvalidGenre() {
        GenreDto genreDto = new GenreDto("INVALID_GENRE_ID", "NAME");
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> genreService.update(genreDto)
        );
        assertThat(entityNotFoundException).message()
                .isEqualTo("Genre with id %s not found".formatted(genreDto.id()));
    }

    @Test
    void shouldDeleteGenre() {
        Genre genre = new Genre("Genre_3");

        mongoTemplate.insert(genre);

        genre = mongoTemplate.findOne(
                Query.query(Criteria.where("name").is("Genre_3")),
                Genre.class
        );
        assertThat(genre).isNotNull();

        genreService.deleteById(genre.getId());

        Genre expectedGenre = mongoTemplate.findById(genre.getId(), Genre.class);
        assertThat(expectedGenre).isNull();
    }

    @Test
    void shouldThrowExceptionWhenDeleteGenre() {
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> genreService.deleteById("INVALID_GENRE_ID")
        );
        assertThat(entityNotFoundException).message()
                .isEqualTo("Genre with id INVALID_GENRE_ID not found");

        Genre genre = mongoTemplate.findOne(
                Query.query(Criteria.where("name").is("Genre_1")),
                Genre.class
        );
        IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> genreService.deleteById(genre.getId())
        );
        assertThat(illegalArgumentException).message()
                .isEqualTo("Genre with id %s should not linked with any books".formatted(genre.getId()));

    }
}
