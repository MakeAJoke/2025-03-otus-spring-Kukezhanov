package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


    @Transactional(readOnly = true)
    @Override
    public Optional<CommentDto> findById(long id) {
        return commentRepository.findById(id).map(commentConverter::commentToDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> findAllByBookId(long id) {
        return commentRepository.findAllByBookId(id).stream().map(commentConverter::commentToDto).toList();
    }

    @Transactional
    @Override
    public CommentDto insert(long bookId, String text) {
        Book book = bookRepository.findById(bookId).orElseThrow(() ->
                new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        Comment comment = new Comment(0, text, book);
        return commentConverter.commentToDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public CommentDto update(long id, String text) {
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Comment with id %d not found".formatted(id)));
        comment.setText(text);
        return commentConverter.commentToDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public void delete(long id) {
        commentRepository.deleteById(id);
    }
}
