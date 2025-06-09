package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuthorRepository.class)
class JpaAuthorRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private JpaAuthorRepository authorRepository;

    @Test
    void shouldReturnAllAuthors() {
        var author1 = new Author(0, "Лев Толстой", null);
        var author2 = new Author(0, "Александр Пушкин", null);

        testEntityManager.persist(author1);
        testEntityManager.persist(author2);
        testEntityManager.flush();
        testEntityManager.clear();

        List<Author> actualAuthors = authorRepository.findAll();
        assertThat(actualAuthors)
                .hasSize(2)
                .extracting(Author::getFullName)
                .contains("Лев Толстой", "Александр Пушкин");
    }

    @Test
    void shouldReturnAuthorById() {
        var expectedAuthor = new Author(0, "Лев Толстой", null);
        testEntityManager.persistAndFlush(expectedAuthor);
        testEntityManager.clear();

        var actualAuthor = authorRepository.findById(expectedAuthor.getId());
        assertThat(actualAuthor).isPresent().get().isEqualTo(expectedAuthor);
    }

    @Test
    void shouldInsertAuthor() {
        var expectedAuthor = new Author(0, "Лев Толстой", null);
        authorRepository.save(expectedAuthor);

        Author actualAuthor = testEntityManager.find(Author.class, expectedAuthor.getId());
        assertThat(actualAuthor).isNotNull().isEqualTo(expectedAuthor);
    }

    @Test
    void shouldUpdateAuthor() {
        var expectedAuthor = new Author(0, "Лев Толстой", null);
        testEntityManager.persistAndFlush(expectedAuthor);
        testEntityManager.clear();

        long authorId = expectedAuthor.getId();
        Optional<Author> authorOptional = authorRepository.findById(authorId);
        assertThat(authorOptional).isPresent();
        testEntityManager.clear();


        Author actualAuthor = authorOptional.get();
        actualAuthor.setFullName("Антон Чехов");
        actualAuthor = authorRepository.save(actualAuthor);
        testEntityManager.flush();
        testEntityManager.clear();

        expectedAuthor = testEntityManager.find(Author.class, actualAuthor.getId());
        assertThat(expectedAuthor).extracting(Author::getFullName).isEqualTo("Антон Чехов");
        assertThat(expectedAuthor).isEqualTo(actualAuthor);
    }

    @Test
    void shouldDeleteAuthor() {
        var author = new Author(0, "Лев Толстой", null);
        testEntityManager.persistAndFlush(author);
        testEntityManager.clear();

        long authorId = author.getId();
        Optional<Author> authorOptional = authorRepository.findById(authorId);
        assertThat(authorOptional).isPresent();
        testEntityManager.clear();

        authorRepository.deleteById(authorId);
        testEntityManager.flush();
        testEntityManager.clear();

        Author deletedAuthor = testEntityManager.find(Author.class, authorId);
        assertThat(deletedAuthor).isNull();


    }

}
