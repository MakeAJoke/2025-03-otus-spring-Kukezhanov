package ru.otus.hw.controllers.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreController.class)
public class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    @Test
    public void shouldReturnAllGenres() throws Exception {
        List<GenreDto> genreDtos = List.of(
                new GenreDto(1, "Genre_1"),
                new GenreDto(2, "Genre_2")
        );

        when(genreService.findAll()).thenReturn(genreDtos);
        mockMvc.perform(get("/api/genre"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                           {
                             "id": 1,
                             "name": "Genre_1"
                           },
                           {
                             "id": 2,
                             "name": "Genre_2"
                           }
                         ]
                        """));
    }


}
