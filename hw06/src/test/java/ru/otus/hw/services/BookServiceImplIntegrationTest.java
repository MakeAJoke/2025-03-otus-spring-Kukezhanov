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
import ru.otus.hw.models.dto.GenreDto;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
        ShellRunnerAutoConfiguration.class
})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class BookServiceImplIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private GenreService genreService;


    @Test
    void shouldInsertAndFindBook() {
        AuthorDto author = authorService.insert("Author_1");
        GenreDto genre = genreService.insert("Genre_1");
        BookDto expectedBook = bookService.insert("new_book", author.id(), Set.of(genre.id()));

        Optional<BookDto> actualBookOptional = bookService.findById(expectedBook.id());

        assertThat(actualBookOptional).isPresent().get().isEqualTo(expectedBook);

        var actualBookDto = actualBookOptional.get();
        assertThatCode(() -> {
            actualBookDto.title();
            actualBookDto.author().fullName();
            actualBookDto.genres().size();
        }).doesNotThrowAnyException();
    }
}
