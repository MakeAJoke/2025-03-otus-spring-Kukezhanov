package ru.otus.hw.controllers.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(AuthorController.class)
public class AuthorControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AuthorService authorService;

    @Test
    public void shouldReturnAllAuthors() {
        List<AuthorDto> authorDtos = List.of(
                new AuthorDto(1, "Author_1"),
                new AuthorDto(2, "Author_2")
        );

        when(authorService.findAll()).thenReturn(Flux.fromIterable(authorDtos));

        webTestClient.get().uri("/api/author")
                .exchange()
                .expectBodyList(AuthorDto.class)
                .isEqualTo(authorDtos);
    }
}
