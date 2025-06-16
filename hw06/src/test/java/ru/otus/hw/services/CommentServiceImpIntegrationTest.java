package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.boot.ShellRunnerAutoConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.models.dto.CommentDto;
import ru.otus.hw.models.dto.GenreDto;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
        ShellRunnerAutoConfiguration.class
})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class CommentServiceImpIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private GenreService genreService;

    @Autowired
    private CommentService commentService;

    @Test
    void shouldSaveAndFindComment() {
        AuthorDto author = authorService.save("Author_1");
        GenreDto genre = genreService.save("Genre_1");
        BookDto book = bookService.create("new_book", author.id(), Set.of(genre.id()));
        CommentDto expectedComment = commentService.save(book.id(), "first_comment");

        Optional<CommentDto> actualCommentOptional = commentService.findById(expectedComment.id());

        assertThat(actualCommentOptional).isPresent().get().isEqualTo(expectedComment);
    }
}
