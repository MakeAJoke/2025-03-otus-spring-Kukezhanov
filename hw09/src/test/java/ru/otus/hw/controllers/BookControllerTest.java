package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.models.dto.CommentDto;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest({BookController.class})
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CommentService commentService;

    @Test
    public void shouldDisplayAllBooks() throws Exception {
        AuthorDto authorDto1 = new AuthorDto(1, "Author_1");
        AuthorDto authorDto2 = new AuthorDto(2, "Author_2");
        GenreDto genreDto1 = new GenreDto(1, "Genre_1");
        GenreDto genreDto2 = new GenreDto(2, "Genre_2");
        GenreDto genreDto3 = new GenreDto(3, "Genre_3");
        List<BookDto> bookDtos = List.of(
                new BookDto(1, "Book_1", authorDto1, List.of(genreDto1, genreDto2)),
                new BookDto(2, "Book_2", authorDto2, List.of(genreDto2, genreDto3)),
                new BookDto(3, "Book_3", authorDto2, List.of(genreDto1, genreDto3))
        );

        given(bookService.findAll()).willReturn(bookDtos);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-list"))
                .andExpect(model().attribute("books", bookDtos));
    }

    @Test
    public void shouldDisplayBookInfoPage() throws Exception {
        AuthorDto authorDto1 = new AuthorDto(1, "Author_1");
        GenreDto genreDto1 = new GenreDto(1, "Genre_1");
        GenreDto genreDto2 = new GenreDto(2, "Genre_2");
        BookDto bookDto = new BookDto(1, "Book_1", authorDto1, List.of(genreDto1, genreDto2));

        List<CommentDto> commentDtos = List.of(
                new CommentDto(1, "first comment"),
                new CommentDto(2, "second comment")
        );

        given(bookService.findById(bookDto.id())).willReturn(Optional.of(bookDto));
        given(commentService.findAllByBookId(bookDto.id())).willReturn(commentDtos);

        mockMvc.perform(get("/books/{id}", bookDto.id()))
                .andExpect(status().isOk())
                .andExpect(view().name("book"))
                .andExpectAll(
                        model().attribute("book", bookDto),
                        model().attribute("comments", commentDtos)
                );
    }

    @Test
    public void shouldDisplayCreateBookPage() throws Exception {
        AuthorDto authorDto1 = new AuthorDto(1, "Author_1");
        AuthorDto authorDto2 = new AuthorDto(1, "Author_1");
        AuthorDto authorDto3 = new AuthorDto(1, "Author_1");
        GenreDto genreDto1 = new GenreDto(1, "Genre_1");
        GenreDto genreDto2 = new GenreDto(2, "Genre_2");
        GenreDto genreDto3 = new GenreDto(2, "Genre_3");
        GenreDto genreDto4 = new GenreDto(2, "Genre_4");
        List<AuthorDto> allAuthorDtos = List.of(
                authorDto1, authorDto2, authorDto3
        );
        List<GenreDto> allGenreDtos = List.of(
                genreDto1, genreDto2, genreDto3, genreDto4
        );

        given(authorService.findAll()).willReturn(allAuthorDtos);
        given(genreService.findAll()).willReturn(allGenreDtos);

        mockMvc.perform(get("/books/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-create"))
                .andExpectAll(
                        model().attribute("authors", allAuthorDtos),
                        model().attribute("allGenres", allGenreDtos)
                );
    }

    @Test
    public void shouldDisplayEditBookPage() throws Exception {
        AuthorDto authorDto1 = new AuthorDto(1, "Author_1");
        AuthorDto authorDto2 = new AuthorDto(1, "Author_1");
        AuthorDto authorDto3 = new AuthorDto(1, "Author_1");
        GenreDto genreDto1 = new GenreDto(1, "Genre_1");
        GenreDto genreDto2 = new GenreDto(2, "Genre_2");
        GenreDto genreDto3 = new GenreDto(2, "Genre_3");
        GenreDto genreDto4 = new GenreDto(2, "Genre_4");
        BookDto bookDto = new BookDto(1, "Book_1", authorDto1, List.of(genreDto1, genreDto2));
        List<AuthorDto> allAuthorDtos = List.of(
                authorDto1, authorDto2, authorDto3
        );
        List<GenreDto> allGenreDtos = List.of(
                genreDto1, genreDto2, genreDto3, genreDto4
        );

        given(bookService.findById(bookDto.id())).willReturn(Optional.of(bookDto));
        given(authorService.findAll()).willReturn(allAuthorDtos);
        given(genreService.findAll()).willReturn(allGenreDtos);

        mockMvc.perform(get("/books/edit?id="+bookDto.id()))
                .andExpect(status().isOk())
                .andExpect(view().name("book-edit"))
                .andExpectAll(
                        model().attribute("book", bookDto),
                        model().attribute("authors", allAuthorDtos),
                        model().attribute("allGenres", allGenreDtos)
                );
    }

    @Test
    public void shouldCreateBookAndRedirectToBookView() throws Exception {
        AuthorDto authorDto1 = new AuthorDto(1, "Author_1");
        GenreDto genreDto1 = new GenreDto(1, "Genre_1");
        GenreDto genreDto2 = new GenreDto(2, "Genre_2");
        BookDto requestBookDto = new BookDto(
                0,
                "Book_1",
                new AuthorDto(1, null),
                List.of(
                        new GenreDto(1, null),
                        new GenreDto(2, null)
                )
        );
        BookDto createdBookDto = new BookDto(
                1,
                "Book_1",
                authorDto1,
                List.of(genreDto1, genreDto2)
        );

        given(bookService.create(requestBookDto)).willReturn(createdBookDto);

        mockMvc.perform(post("/books")
                        .param("id", "0")
                        .param("title", "Book_1")
                        .param("author", "1")
                        .param("genres", "1", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService, times(1)).create(requestBookDto);
    }

    @Test
    public void shouldUpdateBookAndRedirectToBookView() throws Exception {
        AuthorDto authorDto1 = new AuthorDto(1, "Author_1");
        GenreDto genreDto1 = new GenreDto(1, "Genre_1");
        GenreDto genreDto2 = new GenreDto(2, "Genre_2");
        BookDto requestBookDto = new BookDto(
                1,
                "Book_1",
                new AuthorDto(1, null),
                List.of(
                        new GenreDto(1, null),
                        new GenreDto(2, null)
                )
        );
        BookDto bookDto = new BookDto(
                1,
                "Book_1",
                authorDto1,
                List.of(genreDto1, genreDto2)
        );

        given(bookService.findById(requestBookDto.id())).willReturn(Optional.of(bookDto));
        given(bookService.update(requestBookDto)).willReturn(bookDto);
        InOrder inOrder = inOrder(bookService);

        mockMvc.perform(put("/books")
                        .param("id", "1")
                        .param("title", "Book_1")
                        .param("author", "1")
                        .param("genres", "1", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        inOrder.verify(bookService).findById(bookDto.id());
        inOrder.verify(bookService).update(requestBookDto);
    }

    @Test
    public void shouldDeleteBookAndRedirectToBookView() throws Exception {
        doNothing().when(bookService).deleteById(1);
        InOrder inOrder = inOrder(bookService);

        mockMvc.perform(delete("/books")
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService, times(1)).deleteById(1);
    }
}
