package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.dto.CommentDto;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final CommentConverter commentConverter;

    @Override
    public Optional<CommentDto> findById(String id) {
        return commentRepository.findById(id).map(commentConverter::commentToDto);
    }

    @Override
    public List<CommentDto> findAllByBookId(String id) {
        return commentRepository.findAllByBookId(id).stream()
                .map(commentConverter::commentToDto)
                .toList();
    }

    @Override
    public CommentDto create(String bookId, String text) {
        Book book = bookRepository.findById(bookId).orElseThrow(() ->
                new EntityNotFoundException("Book with id %s not found".formatted(bookId)));
        Comment comment = new Comment(text, book);
        return commentConverter.commentToDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto update(CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentDto.id()).orElseThrow(() ->
                new EntityNotFoundException("Comment with id %s not found".formatted(commentDto.id())));
        comment.setText(commentDto.text());
        return commentConverter.commentToDto(commentRepository.save(comment));
    }

    @Override
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }
}
