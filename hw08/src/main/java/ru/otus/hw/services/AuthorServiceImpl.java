package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorConverter authorConverter;

    private final BookRepository bookRepository;

    @Override
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream()
                .map(authorConverter::authorToDto)
                .toList();
    }

    @Override
    public AuthorDto create(AuthorDto authorDto) {
        Author author = new Author(authorDto.fullName());
        author = authorRepository.save(author);
        return authorConverter.authorToDto(author);
    }

    @Override
    public AuthorDto update(AuthorDto authorDto) {
        Author author = authorRepository.findById(authorDto.id()).orElseThrow(() ->
                new EntityNotFoundException("Author with id %s not found".formatted(authorDto.id())));
        author.setFullName(authorDto.fullName());
        authorRepository.save(author);
        List<Book> books = bookRepository.findAllByAuthorId(authorDto.id());
        for (Book book : books) {
            book.setAuthor(author);
            bookRepository.save(book);
        }
        return authorDto;
    }

    @Override
    public void deleteById(String id) {
        authorRepository.deleteById(id);
    }
}