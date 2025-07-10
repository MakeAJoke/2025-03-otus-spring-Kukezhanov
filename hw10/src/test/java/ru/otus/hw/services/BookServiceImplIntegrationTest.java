package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.models.dto.GenreDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class BookServiceImplIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private GenreService genreService;


    @Test
    void shouldCreateAndFindBook() {
        AuthorDto author = new AuthorDto(0,"Author_1");
        author = authorService.save(author);
        GenreDto genre = genreService.save("Genre_1");
        BookDto bookDto = new BookDto(0, "new_book", author, List.of(genre));
        BookDto expectedBook = bookService.create(bookDto);

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
