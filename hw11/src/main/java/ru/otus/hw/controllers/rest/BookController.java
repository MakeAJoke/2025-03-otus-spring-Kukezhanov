package ru.otus.hw.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.dto.BookDto;
import ru.otus.hw.services.BookService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/book")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public Flux<BookDto> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<BookDto>> getBook(@PathVariable long id) {
        return bookService.findById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping
    public Mono<BookDto> createBook(@RequestBody Mono<BookDto> bookDtoMono) {
        return bookService.create(bookDtoMono);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<BookDto>> updateBook(@PathVariable long id, @RequestBody Mono<BookDto> bookDto) {
        return bookService.findById(id)
                .then(bookService.update(bookDto))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping
    public Mono<ResponseEntity<?>> deleteBook(@RequestParam long id) {
        return bookService.deleteById(id).thenReturn(ResponseEntity.ok().build());
    }
}
