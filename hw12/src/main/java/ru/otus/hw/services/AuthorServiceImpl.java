package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.dto.AuthorDto;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorConverter authorConverter;

    @Transactional(readOnly = true)
    @Override
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream().map(authorConverter::authorToDto).toList();
    }

    @Transactional
    @Override
    public AuthorDto save(AuthorDto authorDto) {
        Author author = new Author(authorDto.fullName());
        author = authorRepository.save(author);
        return authorConverter.authorToDto(author);
    }

    @Transactional
    @Override
    public AuthorDto update(AuthorDto authorDto) {
        Author author = authorRepository.findById(authorDto.id()).orElseThrow(() ->
                new EntityNotFoundException("Author with id %d not found".formatted(authorDto.id())));
        author.setFullName(authorDto.fullName());
        return authorDto;
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        authorRepository.deleteById(id);
    }
}