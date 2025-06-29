package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(GenreController.class)
public class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    @Test
    public void shouldDisplayAllGenres() throws Exception {
        List<GenreDto> genreDtos = List.of(
                new GenreDto(1, "Genre_1"),
                new GenreDto(2, "Genre_2"),
                new GenreDto(3, "Genre_3")
        );

        given(genreService.findAll()).willReturn(genreDtos);

        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(view().name("genre-list"))
                .andExpect(model().attribute("genres", genreDtos));
    }

}
