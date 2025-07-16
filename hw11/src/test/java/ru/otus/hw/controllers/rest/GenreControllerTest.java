package ru.otus.hw.controllers.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(GenreController.class)
public class GenreControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GenreService genreService;

    @Test
    public void shouldReturnAllGenres() throws Exception {
        List<GenreDto> genreDtos = List.of(
                new GenreDto(1, "Genre_1"),
                new GenreDto(2, "Genre_2")
        );

        when(genreService.findAll()).thenReturn(Flux.fromIterable(genreDtos));

        webTestClient.get()
                .uri("/api/genre")
                .exchange()
                .expectBodyList(GenreDto.class)
                .isEqualTo(genreDtos);
    }


}
