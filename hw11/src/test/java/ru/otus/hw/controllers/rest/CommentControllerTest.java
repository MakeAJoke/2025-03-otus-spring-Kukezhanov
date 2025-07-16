package ru.otus.hw.controllers.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CommentService commentService;

    @Test
    public void shouldReturnAllBookComments() throws Exception {

        List<CommentDto> commentDtos = List.of(
                new CommentDto(1, "first comment"),
                new CommentDto(2, "second comment")
        );

        when(commentService.findAllByBookId(1)).thenReturn(Flux.fromIterable(commentDtos));

        webTestClient.get()
                .uri("/api/book/1/comment")
                .exchange()
                .expectBodyList(CommentDto.class)
                .isEqualTo(commentDtos);
    }
}
