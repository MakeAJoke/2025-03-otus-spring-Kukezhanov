package ru.otus.hw.controllers.pages;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest({BookPagesController.class})
public class BookPagesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldDisplayAllBooks() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-list"));
    }

    @Test
    public void shouldDisplayBookInfoPage() throws Exception {
        mockMvc.perform(get("/books/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("book"));
    }

    @Test
    public void shouldDisplayCreateBookPage() throws Exception {
        mockMvc.perform(get("/books/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-create"));
    }

    @Test
    public void shouldDisplayEditBookPage() throws Exception {

        mockMvc.perform(get("/books/edit?id=1"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-edit"));
    }
}
