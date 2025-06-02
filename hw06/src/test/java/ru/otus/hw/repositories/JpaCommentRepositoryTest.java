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
@Import(JpaCommentRepository.class)
class JpaCommentRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private JpaCommentRepository commentRepository;

    @Test
    void shouldReturnCommentById() {
        var author = new Author(0, "TestAuthor_1", null);
        var genre = new Genre(0, "TestGenre_1");
        var book = new Book(0, "TestBook_1", author, List.of(genre), null);
        var comment1 = new Comment(0, "first comment text", book);
        var comment2 = new Comment(0, "second comment text", book);

        testEntityManager.persist(author);
        testEntityManager.persist(genre);
        testEntityManager.persist(book);
        testEntityManager.persist(comment1);
        testEntityManager.persist(comment2);
        testEntityManager.flush();

        long id1 = comment1.getId();
        long id2 = comment2.getId();

        testEntityManager.clear();

        var firstComOpt = commentRepository.findById(id1);
        var secondComOpt = commentRepository.findById(id2);

        assertThat(firstComOpt).isPresent().get().isEqualTo(comment1);
        assertThat(secondComOpt).isPresent().get().isEqualTo(comment2);
    }

    @Test
    void shouldReturnAllBookComments() {
        var author = new Author(0, "TestAuthor_1", null);
        var genre = new Genre(0, "TestGenre_1");
        var book = new Book(0, "TestBook_1", author, List.of(genre), null);
        var comment1 = new Comment(0, "first comment text", book);
        var comment2 = new Comment(0, "second comment text", book);
        var comment3 = new Comment(0, "third comment text", book);

        testEntityManager.persist(author);
        testEntityManager.persist(genre);
        testEntityManager.persist(book);
        testEntityManager.persist(comment1);
        testEntityManager.persist(comment2);
        testEntityManager.persist(comment3);
        testEntityManager.flush();

        long bookId = book.getId();
        testEntityManager.clear();

        List<Comment> allBookComments = commentRepository.findAllByBookId(bookId);

        assertThat(allBookComments).hasSize(3).contains(comment1, comment2, comment3);
    }

    @Test
    void shouldInsertComment() {
        var author = new Author(0, "TestAuthor_1", null);
        var genre = new Genre(0, "TestGenre_1");
        var book = new Book(0, "TestBook_1", author, List.of(genre), null);

        testEntityManager.persist(author);
        testEntityManager.persist(genre);
        testEntityManager.persist(book);

        var expectedComment = new Comment(0, "first comment text", book);
        commentRepository.save(expectedComment);
        testEntityManager.flush();

        long commentId = expectedComment.getId();
        testEntityManager.clear();

        Comment actualComment = testEntityManager.find(Comment.class, commentId);
        assertThat(actualComment).isNotNull().isEqualTo(expectedComment);
    }

    @Test
    void shouldUpdateComment() {
        var author = new Author(0, "TestAuthor_1", null);
        var genre = new Genre(0, "TestGenre_1");
        var book = new Book(0, "TestBook_1", author, List.of(genre), null);
        var comment = new Comment(0, "first comment text", book);

        testEntityManager.persist(author);
        testEntityManager.persist(genre);
        testEntityManager.persist(book);
        testEntityManager.persist(comment);
        testEntityManager.flush();

        long commentId = comment.getId();
        testEntityManager.clear();


        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        assertThat(commentOptional).isPresent();
        testEntityManager.clear();


        Comment actualComment = commentOptional.get();
        actualComment.setText("new comment text");
        actualComment = commentRepository.save(actualComment);
        testEntityManager.flush();
        testEntityManager.clear();

        var expectedComment = testEntityManager.find(Comment.class, commentId);
        assertThat(expectedComment).extracting(Comment::getText).isNotEqualTo(comment.getText());
        assertThat(expectedComment).extracting(Comment::getText).isEqualTo(actualComment.getText());
    }

    @Test
    void shouldDeleteComment() {
        var author = new Author(0, "TestAuthor_1", null);
        var genre = new Genre(0, "TestGenre_1");
        var book = new Book(0, "TestBook_1", author, List.of(genre), null);
        var comment = new Comment(0, "first comment text", book);

        testEntityManager.persist(author);
        testEntityManager.persist(genre);
        testEntityManager.persist(book);
        testEntityManager.persist(comment);
        testEntityManager.flush();

        long commentId = comment.getId();
        testEntityManager.clear();

        commentRepository.deleteById(commentId);
        testEntityManager.flush();
        testEntityManager.clear();

        Comment deletedComment = testEntityManager.find(Comment.class, commentId);
        assertThat(deletedComment).isNull();
    }
}
