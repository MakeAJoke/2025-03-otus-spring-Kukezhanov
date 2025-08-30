package ru.otus.hw.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
                new BookDto(1, "Book_1", authorDto1, List.of(genreDto1, genreDto2)),
                new BookDto(2, "Book_2", authorDto2, List.of(genreDto2, genreDto3))
        );
        when(bookService.findAll()).thenReturn(bookDtos);

        mockMvc.perform(get("/api/book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(content().json("""
                        [
                           {
                             "id": 1,
                             "title": "Book_1",
                             "author": { "id": 1, "fullName": "Author_1" },
                             "genres": [
                               { "id": 1, "name": "Genre_1" },
                               { "id": 2, "name": "Genre_2" }
                             ]
                           },
                           {
                             "id": 2,
                             "title": "Book_2",
                             "author": { "id": 2, "fullName": "Author_2" },
                             "genres": [
                               { "id": 2, "name": "Genre_2" },
                               { "id": 3, "name": "Genre_3" }
                             ]
                           }
                         ]
                        """));
    }


    @Test
    public void shouldReturnBookById() throws Exception {
        AuthorDto authorDto1 = new AuthorDto(1, "Author_1");
        GenreDto genreDto1 = new GenreDto(1, "Genre_1");
        GenreDto genreDto2 = new GenreDto(2, "Genre_2");
        BookDto bookDto = new BookDto(1, "Book_1", authorDto1, List.of(genreDto1, genreDto2));
        when(bookService.findById(bookDto.id())).thenReturn(Optional.of(bookDto));

        mockMvc.perform(get("/api/book/" + bookDto.id()))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                       {
                         "id": 1,
                         "title": "Book_1",
                         "author": { "id": 1, "fullName": "Author_1" },
                         "genres": [
                           { "id": 1, "name": "Genre_1" },
                           { "id": 2, "name": "Genre_2" }
                         ]
                       }
                       """));
    }

    @Test
    public void shouldReturnCreatedBook() throws Exception {
        AuthorDto authorDto1 = new AuthorDto(1, "Author_1");
        GenreDto genreDto1 = new GenreDto(1, "Genre_1");
        GenreDto genreDto2 = new GenreDto(2, "Genre_2");
        BookDto bookDto = new BookDto(0, "Book_1", authorDto1, List.of(genreDto1, genreDto2));
        BookDto createdBookDto = new BookDto(1, "Book_1", authorDto1, List.of(genreDto1, genreDto2));
        when(bookService.create(bookDto)).thenReturn(createdBookDto);

        mockMvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookDto)))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                         "id": 1,
                         "title": "Book_1",
                         "author": { "id": 1, "fullName": "Author_1" },
                         "genres": [
                           { "id": 1, "name": "Genre_1" },
                           { "id": 2, "name": "Genre_2" }
                         ]
                       }
                       """));
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

        when(bookService.findById(bookDto.id())).thenReturn(Optional.of(bookDto));
        when(bookService.update(updatedBookDto)).thenReturn(updatedBookDto);

        mockMvc.perform(put("/api/book/" + bookDto.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedBookDto)))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                         "id": 1,
                         "title": "Book_2",
                         "author": { "id": 2, "fullName": "Author_2" },
                         "genres": [
                           { "id": 1, "name": "Genre_1" },
                           { "id": 3, "name": "Genre_3" }
                         ]
                       }
                       """));
    }

    @Test
    public void shouldReturnOkWhenDeleteBook() throws Exception {
        doNothing().when(bookService).deleteById(1);
        mockMvc.perform(delete("/api/book?id=1"))
                .andExpect(status().isOk());
    }
}
