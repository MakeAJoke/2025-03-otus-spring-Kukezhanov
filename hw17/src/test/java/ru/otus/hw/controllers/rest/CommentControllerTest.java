package ru.otus.hw.controllers.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.models.dto.CommentDto;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Test
    public void shouldReturnAllBookComments() throws Exception {

        List<CommentDto> commentDtos = List.of(
                new CommentDto(1, "first comment"),
                new CommentDto(2, "second comment")
        );

        when(commentService.findAllByBookId(1)).thenReturn(commentDtos);
        mockMvc.perform(get("/api/book/1/comment"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                           {
                             "id": 1,
                             "text": "first comment"
                           },
                           {
                             "id": 2,
                             "text": "second comment"
                           }
                         ]
                        """));

    }
}
