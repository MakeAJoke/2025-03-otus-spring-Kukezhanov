package ru.otus.hw.services;

import ru.otus.hw.models.Genre;

import java.util.List;

public interface GenreService {
    List<Genre> findAll();

    Genre insert(String name);

    Genre update(long id, String name);

    void deleteById(long id);
}
