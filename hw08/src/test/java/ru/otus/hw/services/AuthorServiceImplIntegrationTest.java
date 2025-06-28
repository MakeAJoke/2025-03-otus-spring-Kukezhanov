package ru.otus.hw.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.shell.boot.ShellRunnerAutoConfiguration;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.dto.AuthorDto;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
        ShellRunnerAutoConfiguration.class
})
class AuthorServiceImplIntegrationTest {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AuthorConverter authorConverter;

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
    void shouldReturnAllAuthors() {
        List<AuthorDto> authors = authorService.findAll();

        assertThat(authors).hasSize(3)
                .extracting(AuthorDto::fullName)
                .contains("Author_1", "Author_2", "Author_3");
    }


    @Test
    void shouldCreateAndReturnAuthor() {
        AuthorDto authorDto = new AuthorDto(null,"NEW_AUTHOR");
        var expectedAuthorDto = authorService.create(authorDto);

        List<Author> allAuthors = mongoTemplate.findAll(Author.class);
        assertThat(allAuthors).hasSize(4);

        Author author = mongoTemplate.findOne(
                Query.query(
                        Criteria.where("fullName").is("NEW_AUTHOR")
                ), Author.class
        );

        AuthorDto actualAuthorDto = authorConverter.authorToDto(author);
        assertThat(actualAuthorDto).isEqualTo(expectedAuthorDto);
    }

    @Test
    void shouldUpdateAuthor() {
        Author author = mongoTemplate.findOne(
                Query.query(
                        Criteria.where("fullName").is("Author_1")
                ), Author.class
        );

        AuthorDto existingAuthorDto = authorConverter.authorToDto(author);
        AuthorDto expectedAuthorDto = new AuthorDto(existingAuthorDto.id(), "UPDATE_AUTHOR");

        authorService.update(expectedAuthorDto);

        Author actualAuthor = mongoTemplate.findOne(
                Query.query(
                        Criteria.where("fullName").is("Author_1")
                ), Author.class
        );
        assertThat(actualAuthor).isNull();

        actualAuthor = mongoTemplate.findOne(
                Query.query(
                        Criteria.where("fullName").is("UPDATE_AUTHOR")
                ), Author.class
        );
        AuthorDto actualAuthorDto = authorConverter.authorToDto(actualAuthor);
        assertThat(actualAuthorDto).isEqualTo(expectedAuthorDto);

        Book updatedBook = mongoTemplate.findOne(
                Query.query(Criteria.where("author.fullName").is("UPDATE_AUTHOR")),
                Book.class
        );

        assertThat(updatedBook).isNotNull();
    }

    @Test
    void shouldThrowAndEntityNotFoundExceptionWhenAttemptToUpdateAbsentAuthor() {
        AuthorDto authorDto = new AuthorDto("existingAuthor","NEW_AUTHOR");
        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> authorService.update(authorDto)
        );
        assertThat(entityNotFoundException).message().isEqualTo("Author with id existingAuthor not found");
    }

    @Test
    void shouldDeleteAuthor() {
        Author author = mongoTemplate.findOne(
                Query.query(
                        Criteria.where("fullName").is("Author_1")
                ), Author.class
        );
        assertThat(author).isNotNull();

        List<Book> authorsBooks = mongoTemplate.find(
                Query.query(Criteria.where("author.id").is(author.getId())),
                Book.class
        );
        assertThat(authorsBooks).hasSize(1);

        authorService.deleteById(author.getId());

        Author expectedAuthor = mongoTemplate.findById(author.getId(), Author.class);
        assertThat(expectedAuthor).isNull();

        List<Book> expectedBooks = mongoTemplate.find(
                Query.query(Criteria.where("author.id").is(author.getId())),
                Book.class
        );
        assertThat(expectedBooks).hasSize(0);
    }
}
