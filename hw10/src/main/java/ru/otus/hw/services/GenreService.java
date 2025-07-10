package ru.otus.hw.services;

import ru.otus.hw.models.dto.GenreDto;

import java.util.List;

public interface GenreService {
    List<GenreDto> findAll();

    GenreDto save(String name);

    GenreDto update(long id, String name);

    void deleteById(long id);
}
