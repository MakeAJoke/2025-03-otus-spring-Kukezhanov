package ru.otus.hw.controllers.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.services.BookService;

import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebFluxTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BookService bookService;

    @Test
    public void shouldReturnAllBooks() throws Exception {
        AuthorDto authorDto1 = new AuthorDto(1, "Author_1");
        AuthorDto authorDto2 = new AuthorDto(2, "Author_2");
        GenreDto genreDto1 = new GenreDto(1, "Genre_1");
        GenreDto genreDto2 = new GenreDto(2, "Genre_2");
        GenreDto genreDto3 = new GenreDto(3, "Genre_3");

        List<BookDto> bookDtos = List.of(
                new BookDto(1L, "Book_1", authorDto1, List.of(genreDto1, genreDto2)),
                new BookDto(2L, "Book_2", authorDto2, List.of(genreDto2, genreDto3))
        );

        when(bookService.findAll()).thenReturn(Flux.fromIterable(bookDtos));

        webTestClient.get().uri("/api/book")
                .exchange()
                .expectBodyList(BookDto.class)
                .isEqualTo(bookDtos);

    }


    @Test
    public void shouldReturnBookById() throws Exception {
        AuthorDto authorDto1 = new AuthorDto(1, "Author_1");
        GenreDto genreDto1 = new GenreDto(1, "Genre_1");
        GenreDto genreDto2 = new GenreDto(2, "Genre_2");
        BookDto bookDto = new BookDto(1, "Book_1", authorDto1, List.of(genreDto1, genreDto2));

        when(bookService.findById(bookDto.id())).thenReturn(Mono.just(bookDto));

        webTestClient.get().uri("/api/book/" + bookDto.id())
                .exchange()
                .expectBody(BookDto.class)
                .isEqualTo(bookDto);
    }

    @Test
    public void shouldReturnCreatedBook() throws Exception {
        AuthorDto authorDto1 = new AuthorDto(1, "Author_1");
        GenreDto genreDto1 = new GenreDto(1, "Genre_1");
        GenreDto genreDto2 = new GenreDto(2, "Genre_2");
        BookDto bookDto = new BookDto(0, "Book_1", authorDto1, List.of(genreDto1, genreDto2));
        BookDto createdBookDto = new BookDto(1, "Book_1", authorDto1, List.of(genreDto1, genreDto2));

        when(bookService.create(any())).thenReturn(Mono.just(createdBookDto));

        webTestClient.post()
                .uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookDto)
                .exchange()
                .expectBody(BookDto.class)
                .isEqualTo(createdBookDto);
    }

    @Test
    public void shouldUpdateAndReturnBook() throws Exception {
        AuthorDto authorDto1 = new AuthorDto(1, "Author_1");
        AuthorDto authorDto2 = new AuthorDto(2, "Author_2");
        GenreDto genreDto1 = new GenreDto(1, "Genre_1");
        GenreDto genreDto2 = new GenreDto(2, "Genre_2");
        GenreDto genreDto3 = new GenreDto(3, "Genre_3");
        BookDto bookDto = new BookDto(1, "Book_1", authorDto1, List.of(genreDto1, genreDto2));
        BookDto updatedBookDto = new BookDto(1, "Book_2", authorDto2, List.of(genreDto1, genreDto3));

        when(bookService.findById(bookDto.id())).thenReturn(Mono.just(bookDto));
        when(bookService.update(any())).thenReturn(Mono.just(updatedBookDto));

        webTestClient.put()
                .uri("/api/book/" + bookDto.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookDto)
                .exchange()
                .expectBody(BookDto.class)
                .isEqualTo(updatedBookDto);
    }

    @Test
    public void shouldReturnOkWhenDeleteBook() throws Exception {
        when(bookService.deleteById(anyLong())).thenReturn(Mono.empty());
        webTestClient.delete()
                .uri("/api/book?id=1")
                .exchange()
                .expectStatus()
                .isOk();
    }
}
