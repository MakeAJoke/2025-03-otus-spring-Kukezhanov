package ru.otus.hw.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.dto.GenreDto;
import ru.otus.hw.services.GenreService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/genre")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public Flux<GenreDto> getAllGenres() {
        return genreService.findAll();
    }
}
