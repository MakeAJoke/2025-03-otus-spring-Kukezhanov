package ru.otus.hw.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.dto.CommentDto;
import ru.otus.hw.services.CommentService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/book/{bookId}/comment")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public Flux<CommentDto> getAllBookComments(@PathVariable long bookId) {
        return commentService.findAllByBookId(bookId);
    }
}
