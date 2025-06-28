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
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.dto.CommentDto;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
        ShellRunnerAutoConfiguration.class
})
class CommentServiceImplIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CommentConverter commentConverter;

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

        Comment comment1 = new Comment("FIRST_COMMENT", book1);
        Comment comment2 = new Comment("SECOND_COMMENT", book1);
        Comment comment3 = new Comment("THIRD_COMMENT", book2);
        Comment comment4 = new Comment("FOURTH_COMMENT", book2);
        Comment comment5 = new Comment("FIFTH_COMMENT", book3);

        mongoTemplate.insert(comment1);
        mongoTemplate.insert(comment2);
        mongoTemplate.insert(comment3);
        mongoTemplate.insert(comment4);
        mongoTemplate.insert(comment5);
    }

    @Test
    void shouldReturnCommentById() {
        Comment comment = mongoTemplate.findOne(
                Query.query(Criteria.where("text").is("FIRST_COMMENT")),
                Comment.class
        );

        CommentDto actualCommentDto = commentService.findById(comment.getId()).orElse(null);

        assertThat(actualCommentDto).isNotNull()
                .extracting(CommentDto::text)
                .isEqualTo("FIRST_COMMENT");
    }


    @Test
    void shouldReturnAllByBook() {
        Book book = mongoTemplate.findOne(
                Query.query(
                        Criteria.where("title").is("Book_1")
                ), Book.class
        );


        List<CommentDto> commentDtos = commentService.findAllByBookId(book.getId());
        assertThat(commentDtos.size()).isEqualTo(2);
        assertThat(commentDtos)
                .extracting(CommentDto::text)
                .contains("FIRST_COMMENT", "SECOND_COMMENT");
    }

    @Test
    void shouldCreateAndReturnComment() {
        Book book = mongoTemplate.findOne(
                Query.query(
                        Criteria.where("title").is("Book_2")
                ), Book.class
        );
        CommentDto expectedComment = commentService.create(book.getId(), "NEW_COMMENT");

        Comment actualComment = mongoTemplate.findOne(
                Query.query(
                        Criteria.where("id").is(expectedComment.id())
                ), Comment.class
        );

        assertThat(actualComment).isNotNull();
        assertThat(actualComment).extracting(Comment::getId).isEqualTo(expectedComment.id());
        assertThat(actualComment).extracting(Comment::getText).isEqualTo(expectedComment.text());
    }

    @Test
    void shouldThrowExceptionWhenCreateCommentForInvalidBook() {
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> commentService.create("INVALID_BOOK_ID", "NEW_COMMENT")
        );
        assertThat(entityNotFoundException).message().isEqualTo("Book with id INVALID_BOOK_ID not found");
    }

    @Test
    void shouldUpdateComment() {
        Comment comment = mongoTemplate.findOne(
                Query.query(Criteria.where("text").is("FIRST_COMMENT")),
                Comment.class
        );

        assertThat(comment).isNotNull();

        comment.setText("UPDATED_TEXT_COMMENT");

        CommentDto commentDto = commentConverter.commentToDto(comment);

        CommentDto expectedCommentDto = commentService.update(commentDto);

        Comment actualComment = mongoTemplate.findOne(
                Query.query(Criteria.where("text").is("FIRST_COMMENT")),
                Comment.class
        );
        assertThat(actualComment).isNull();

        actualComment = mongoTemplate.findOne(
                Query.query(new Criteria().andOperator(
                                Criteria.where("id").is(expectedCommentDto.id()),
                                Criteria.where("text").is("UPDATED_TEXT_COMMENT")
                        )
                ),
                Comment.class
        );
        assertThat(actualComment).isNotNull();
    }

    @Test
    void shouldDeleteComment() {
        Comment comment = mongoTemplate.findOne(
                Query.query(Criteria.where("text").is("FIRST_COMMENT")),
                Comment.class
        );
        assertThat(comment).isNotNull();

        commentService.deleteById(comment.getId());

        Comment expectedComment = mongoTemplate.findById(comment.getId(), Comment.class);
        assertThat(expectedComment).isNull();
    }

    @Test
    void shouldThrowExceptionWhenUpdateInvalidComment() {
        CommentDto commentDto = new CommentDto("INVALID_COMMENT_ID", "TEXT");

        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> commentService.update(commentDto)
        );
        assertThat(entityNotFoundException).message()
                .isEqualTo("Comment with id INVALID_COMMENT_ID not found");

    }
}
