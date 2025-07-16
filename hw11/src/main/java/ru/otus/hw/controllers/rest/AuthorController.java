package ru.otus.hw.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/author")
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public Flux<AuthorDto> getAllAuthors() {
        return authorService.findAll();
    }

}
