package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.models.dto.CommentDto;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@WebMvcTest(BookController.class)
@Import(SecurityConfiguration.class)
public class BookControllerSecurityTest {

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
    public void shouldRedirectToLoginWhenRequestBookEndpointsAsUnauthenticated() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/books/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(post("/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(delete("/books").param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    public void shouldReturn403WhenEnterToCreateUpdatePagesWithoutAdminRole() throws Exception {
        mockMvc.perform(get("/books/create"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/books/edit/1"))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "user", authorities = {"ADMIN"})
    @Test
    public void shouldGetAccessWhenEnterToCreateUpdatePagesWithAdminRole() throws Exception {
        mockMvc.perform(get("/books/create"))
                .andExpect(status().isOk());

        BookDto bookDto = new BookDto(1, "Book_1", null, Collections.emptyList());

        given(bookService.findById(bookDto.id())).willReturn(Optional.of(bookDto));

        mockMvc.perform(get("/books/edit?id=1"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnBookEndpointsWithoutModifyingButtonWhenUserIsNotAdmin() throws Exception {
        var admin = user("user").authorities(new SimpleGrantedAuthority("ADMIN"));
        var user = user("user").authorities(new SimpleGrantedAuthority("USER"));

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

        mockMvc.perform(get("/books").with(user))
                .andExpect(status().isOk())
                .andExpect(xpath("//a[contains(@href, '/books/create')]").doesNotExist())
                .andExpect(xpath("//*[contains(@class, 'delete-action')]").doesNotExist());

        mockMvc.perform(get("/books/1").with(user))
                .andExpect(status().isOk())
                .andExpect(xpath("//a[contains(@href, '/books/edit')]").doesNotExist())
                .andExpect(xpath("//form[@action='/books?id=1']").doesNotExist());

        mockMvc.perform(get("/books").with(admin))
                .andExpect(status().isOk())
                .andExpect(xpath("//a[contains(@href, '/books/create')]").exists())
                .andExpect(xpath("//*[contains(@class, 'delete-action')]").exists());

        mockMvc.perform(get("/books/1").with(admin))
                .andExpect(status().isOk())
                .andExpect(xpath("//a[contains(@href, '/books/edit')]").exists())
                .andExpect(xpath("//form[@action='/books?id=1']").exists());
    }
}
