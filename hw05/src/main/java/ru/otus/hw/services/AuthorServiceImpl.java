package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Author insert(String fullName) {
        return authorRepository.save(new Author(0, fullName));
    }

    @Override
    public Author update(long id, String fullName) {
        return authorRepository.save(new Author(id, fullName));
    }

    @Override
    public void deleteById(long id) {
        authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(id)));
        bookRepository.findAll().stream()
                .filter(book -> book.getAuthor().getId() == id)
                .findAny()
                .ifPresent(book -> {
                    throw new IllegalArgumentException("Author with id %d should not have any books".formatted(id));
                });
        authorRepository.deleteById(id);
    }
}