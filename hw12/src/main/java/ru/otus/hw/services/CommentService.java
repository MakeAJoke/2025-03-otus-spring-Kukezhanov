package ru.otus.hw.services;

import ru.otus.hw.models.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    Optional<CommentDto> findById(long id);

    List<CommentDto> findAllByBookId(long id);

    CommentDto save(long bookId, String text);

    CommentDto update(long id, String text);

    void delete(long id);
}
