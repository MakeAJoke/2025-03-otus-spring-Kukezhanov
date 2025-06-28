package ru.otus.hw.services;

import ru.otus.hw.models.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    Optional<CommentDto> findById(String id);

    List<CommentDto> findAllByBookId(String id);

    CommentDto create(String bookId, String text);

    CommentDto update(CommentDto commentDto);

    void deleteById(String id);
}
