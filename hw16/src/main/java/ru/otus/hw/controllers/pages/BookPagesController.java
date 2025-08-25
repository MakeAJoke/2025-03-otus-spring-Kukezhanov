package ru.otus.hw.controllers.pages;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/books")
public class BookPagesController {

    @GetMapping
    public String listPage() {
        return "book-list";
    }

    @GetMapping("/{id}")
    public String viewPage(@PathVariable long id) {
        return "book";
    }

    @GetMapping("/create")
    public String createPage() {
        return "book-create";
    }

    @GetMapping("/edit")
    public String editPage(@RequestParam("id") long id) {
        return "book-edit";
    }
}
