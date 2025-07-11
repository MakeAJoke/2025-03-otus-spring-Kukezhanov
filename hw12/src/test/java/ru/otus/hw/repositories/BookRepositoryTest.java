package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldReturnAllBooks() {
        var author1 = new Author("TestAuthor_1");
        var author2 = new Author("TestAuthor_2");
        var genre1 = new Genre(0, "TestGenre_1");
        var genre2 = new Genre(0, "TestGenre_2");
        var genre3 = new Genre(0, "TestGenre_3");
        var expectedBook1 = new Book(0, "TestBook_1", author1, List.of(genre1, genre2), null);
        var expectedBook2 = new Book(0, "TestBook_2", author2, List.of(genre2, genre3), null);

        testEntityManager.persist(author1);
        testEntityManager.persist(author2);
        testEntityManager.persist(genre1);
        testEntityManager.persist(genre2);
        testEntityManager.persist(genre3);
        testEntityManager.persist(expectedBook1);
        testEntityManager.persist(expectedBook2);
        testEntityManager.flush();
        testEntityManager.clear();

        List<Book> actualBooks = bookRepository.findAll();
        assertThat(actualBooks)
                .hasSize(2)
                .contains(expectedBook1, expectedBook2);
    }

    @Test
    void shouldReturnBookById() {
        var author = new Author("TestAuthor_1");
        var genre1 = new Genre(0, "TestGenre_1");
        var genre2 = new Genre(0, "TestGenre_2");
        var book = new Book(0, "TestBook_1", author, List.of(genre1, genre2), null);
        testEntityManager.persist(author);
        testEntityManager.persist(genre1);
        testEntityManager.persist(genre2);
        testEntityManager.persist(book);
        testEntityManager.flush();
        testEntityManager.clear();

        var bookId = book.getId();
        var actualBook = bookRepository.findById(bookId);
        testEntityManager.clear();
        var expectedBook = testEntityManager.find(Book.class, bookId);
        assertThat(actualBook).isPresent().get().isEqualTo(expectedBook);
    }

    @Test
    void shouldInsertBook() {
        var author = new Author("TestAuthor_1");
        var genre1 = new Genre(0, "TestGenre_1");
        var genre2 = new Genre(0, "TestGenre_2");
        testEntityManager.persist(author);
        testEntityManager.persist(genre1);
        testEntityManager.persist(genre2);

        var expectedBook = new Book(0, "TestBook_1", author, List.of(genre1, genre2), null);
        bookRepository.save(expectedBook);
        testEntityManager.flush();
        testEntityManager.clear();

        Book actualBook = testEntityManager.find(Book.class, expectedBook.getId());
        assertThat(actualBook).isNotNull().isEqualTo(expectedBook);
    }

    @Test
    void shouldUpdateBook() {
        var author1 = new Author("TestAuthor_1");
        var author2 = new Author("TestAuthor_2");
        var genre1 = new Genre(0, "TestGenre_1");
        var genre2 = new Genre(0, "TestGenre_2");
        var genre3 = new Genre(0, "TestGenre_3");
        var book = new Book(0, "TestBook_1", author1, List.of(genre1, genre2), null);

        testEntityManager.persist(author1);
        testEntityManager.persist(author2);
        testEntityManager.persist(genre1);
        testEntityManager.persist(genre2);
        testEntityManager.persist(genre3);
        testEntityManager.persist(book);
        testEntityManager.flush();
        testEntityManager.clear();

        long bookId = book.getId();
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        assertThat(bookOptional).isPresent();
        testEntityManager.clear();


        Book actualBook = bookOptional.get();
        actualBook.setTitle("New Title");
        actualBook.setAuthor(author2);
        actualBook.setGenres(List.of(genre1, genre3));
        actualBook = bookRepository.save(actualBook);
        testEntityManager.flush();
        testEntityManager.clear();

        var expectedBook = testEntityManager.find(Book.class, actualBook.getId());
        assertThat(expectedBook).extracting(Book::getTitle).isEqualTo("New Title");
        assertThat(expectedBook.getGenres()).hasSize(2).contains(genre1, genre3);
        assertThat(expectedBook).isEqualTo(actualBook);
    }

    @Test
    void shouldDeleteBook() {
        var author = new Author("TestAuthor_1");
        var genre1 = new Genre(0, "TestGenre_1");
        var genre2 = new Genre(0, "TestGenre_2");
        var book = new Book(0, "TestBook_1", author, List.of(genre1, genre2), null);
        testEntityManager.persist(author);
        testEntityManager.persist(genre1);
        testEntityManager.persist(genre2);
        testEntityManager.persist(book);
        testEntityManager.flush();
        testEntityManager.clear();

        long bookId = book.getId();
        bookRepository.deleteById(bookId);
        testEntityManager.flush();
        testEntityManager.clear();

        Book deletedBook = testEntityManager.find(Book.class, bookId);
        assertThat(deletedBook).isNull();
    }

}
