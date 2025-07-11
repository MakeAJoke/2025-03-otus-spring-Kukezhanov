package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(AuthorController.class)
@Import(SecurityConfiguration.class)
@WithMockUser(username = "admin")
public class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @Test
    public void shouldDisplayAllAuthors() throws Exception {
        List<AuthorDto> authorDtoList = List.of(
                new AuthorDto(1, "Author_1"),
                new AuthorDto(2, "Author_2")
        );
        given(authorService.findAll()).willReturn(authorDtoList);
        mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(view().name("author-list"))
                .andExpect(model().attribute("authors", authorDtoList));
    }
}
