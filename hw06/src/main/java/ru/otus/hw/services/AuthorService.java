package ru.otus.hw.services;

import ru.otus.hw.models.dto.AuthorDto;

import java.util.List;

public interface AuthorService {
    List<AuthorDto> findAll();

    AuthorDto insert(String fullName);

    AuthorDto update(long id, String fullName);

    void deleteById(long id);
}
