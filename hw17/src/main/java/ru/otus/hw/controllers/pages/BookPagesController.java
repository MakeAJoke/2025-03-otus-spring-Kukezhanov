package ru.otus.hw.controllers.pages;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.hw.services.BookService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/books")
public class BookPagesController {

    private final BookService bookService;

    @GetMapping
    public String listPage() {
        return "book-list";
    }

    @GetMapping("/{id}")
    public String viewPage(@PathVariable long id) {
        bookService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        return "book";
    }

    @GetMapping("/create")
    public String createPage() {
        return "book-create";
    }

    @GetMapping("/edit")
    public String editPage(@RequestParam("id") long id) {
        bookService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        return "book-edit";
    }
}
