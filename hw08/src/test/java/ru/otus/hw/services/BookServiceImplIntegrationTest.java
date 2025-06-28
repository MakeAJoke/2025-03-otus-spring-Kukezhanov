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
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.models.dto.GenreDto;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
        ShellRunnerAutoConfiguration.class
})
class BookServiceImplIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BookConverter bookConverter;

    @Autowired
    private AuthorConverter authorConverter;

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
    void shouldReturnBookById() {
        Author author = mongoTemplate.findOne(
                Query.query(Criteria.where("fullName").is("Author_1")),
                Author.class
        );
        Genre genre = mongoTemplate.findOne(
                Query.query(Criteria.where("name").is("Genre_1")), Genre.class
        );

        Book book = new Book(null, "NEW_BOOK", author, List.of(genre), Collections.emptyList());
        mongoTemplate.save(book);


        BookDto actualBook = bookService.findById(book.getId()).orElse(null);
        assertThat(actualBook).isNotNull()
                .extracting(BookDto::title)
                .isEqualTo("NEW_BOOK");
        assertThat(actualBook).extracting(BookDto::author)
                .extracting(AuthorDto::fullName)
                .isEqualTo("Author_1");

        List<GenreDto> bookGenres = actualBook.genres();
        assertThat(bookGenres).hasSize(1)
                .extracting(GenreDto::name)
                .contains("Genre_1");
    }

    @Test
    void shouldReturnAllBooks() {
        List<BookDto> bookDtos = bookService.findAll();
        assertThat(bookDtos.size()).isEqualTo(3);
        assertThat(bookDtos)
                .extracting(BookDto::title)
                .contains("Book_1", "Book_2", "Book_3");
        assertThat(bookDtos)
                .extracting(BookDto::author)
                .extracting(AuthorDto::fullName)
                .contains("Author_1", "Author_2", "Author_3");
    }

    @Test
    void shouldCreateAndReturnBook() {
        Author author = mongoTemplate.findOne(
                Query.query(Criteria.where("fullName").is("Author_1")),
                Author.class
        );
        Genre genre = mongoTemplate.findOne(
                Query.query(Criteria.where("name").is("Genre_1")), Genre.class
        );

        BookDto bookDto = new BookDto(
                null,
                "NEW_BOOK",
                authorConverter.authorToDto(author),
                List.of(genreConverter.genreToDto(genre))
        );
        var expectedBookDto = bookService.create(bookDto);

        List<Book> allBooks = mongoTemplate.findAll(Book.class);
        assertThat(allBooks).hasSize(4);

        Book actualBook = mongoTemplate.findOne(
                Query.query(
                        Criteria.where("id").is(expectedBookDto.id())
                ), Book.class
        );

        BookDto actualBookDto =bookConverter.bookToDto(actualBook);
        assertThat(actualBookDto).isEqualTo(expectedBookDto);
    }

    @Test
    void shouldUpdateBook() {
        Book book = mongoTemplate.findOne(
                Query.query(
                        Criteria.where("title").is("Book_1")
                ), Book.class
        );
        BookDto existingBookDto = bookConverter.bookToDto(book);

        Author newAuthor = mongoTemplate.findOne(
                Query.query(Criteria.where("fullName").is("Author_2")),
                Author.class
        );
        Genre newGenre = new Genre("Genre_3");
        newGenre = mongoTemplate.save(newGenre);

        BookDto bookDto = new BookDto(
                existingBookDto.id(),
                "UPDATED_BOOK",
                authorConverter.authorToDto(newAuthor),
                List.of(genreConverter.genreToDto(newGenre))
        );

        BookDto expecteBookDto = bookService.update(bookDto);

        Book actualBook = mongoTemplate.findOne(
                Query.query(
                        new Criteria().andOperator(
                                Criteria.where("title").is("Book_1"),
                                Criteria.where("author.fullName").is("Author_1"),
                                Criteria.where("genres.name").is("Genre_1")
                        )
                ), Book.class
        );
        assertThat(actualBook).isNull();

        actualBook = mongoTemplate.findOne(
                Query.query(
                        new Criteria().andOperator(
                                Criteria.where("title").is("UPDATED_BOOK"),
                                Criteria.where("author.fullName").is("Author_2"),
                                Criteria.where("genres.name").is("Genre_3")
                        )
                ), Book.class
        );
        assertThat(actualBook).isNotNull();
    }

    @Test
    void shouldDeleteBook() {

        Book book = mongoTemplate.findOne(
                Query.query(
                        Criteria.where("title").is("Book_1")
                ), Book.class
        );
        assertThat(book).isNotNull();


        bookService.deleteById(book.getId());

        Book expectedBook = mongoTemplate.findById(book.getId(), Book.class);
        assertThat(expectedBook).isNull();
    }

    @Test
    void shouldThrowExceptionWhenSaveInvalidBook() {
        BookDto bookDtoWithoutGenres = new BookDto(
                null,
                "NEW_BOOK",
                new AuthorDto("INVALID_AUTHOR_ID", "Author"),
                Collections.emptyList());

        Genre genre = mongoTemplate.findOne(
                Query.query(Criteria.where("name").is("Genre_1")), Genre.class
        );
        BookDto bookDtoWithInvalidAuthor = new BookDto(
                null,
                "NEW_BOOK",
                new AuthorDto("INVALID_AUTHOR_ID", "Author"),
                List.of(genreConverter.genreToDto(genre)));

        Author author = mongoTemplate.findOne(
                Query.query(Criteria.where("fullName").is("Author_1")), Author.class
        );
        BookDto bookDtoWithInvalidGenres = new BookDto(
                null,
                "NEW_BOOK",
                new AuthorDto(author.getId(), author.getFullName()),
                List.of(new GenreDto("INVALID_ID", "Genre")));

        BookDto bookDtoWithInvalidId = new BookDto(
                "INVALID_BOOK_ID",
                "NEW_BOOK",
                new AuthorDto(author.getId(), author.getFullName()),
                List.of(genreConverter.genreToDto(genre)));

        IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.create(bookDtoWithoutGenres)
        );
        assertThat(illegalArgumentException).message()
                .isEqualTo("Genres ids must not be null");

        EntityNotFoundException authorEntityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.create(bookDtoWithInvalidAuthor)
        );
        assertThat(authorEntityNotFoundException).message()
                .isEqualTo("Author with id INVALID_AUTHOR_ID not found");

        EntityNotFoundException genreEntityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.create(bookDtoWithInvalidGenres)
        );
        assertThat(genreEntityNotFoundException).message()
                .isEqualTo("One or all genres with ids [INVALID_ID] not found");

        EntityNotFoundException bookEntityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.update(bookDtoWithInvalidId)
        );
        assertThat(bookEntityNotFoundException).message()
                .isEqualTo("Book with id INVALID_BOOK_ID not found");

    }
}
