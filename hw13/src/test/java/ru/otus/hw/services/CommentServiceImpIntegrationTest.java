package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.models.dto.CommentDto;
import ru.otus.hw.models.dto.GenreDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
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
        AuthorDto author = new AuthorDto(0, "Author_1");
        author = authorService.save(author);
        GenreDto genre = genreService.save("Genre_1");
        BookDto bookDto = new BookDto(0, "new_book", author, List.of(genre));
        BookDto book = bookService.create(bookDto);
        CommentDto expectedComment = commentService.save(book.id(), "first_comment");

        Optional<CommentDto> actualCommentOptional = commentService.findById(expectedComment.id());

        assertThat(actualCommentOptional).isPresent().get().isEqualTo(expectedComment);
    }
}
