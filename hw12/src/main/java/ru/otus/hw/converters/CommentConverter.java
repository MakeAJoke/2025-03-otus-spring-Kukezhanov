package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.dto.CommentDto;

@RequiredArgsConstructor
@Component
public class CommentConverter {

    public String commentToString(Comment comment) {
        return "Id: %d, text: %s".formatted(
                comment.getId(),
                comment.getText());
    }

    public CommentDto commentToDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText());
    }

    public String commentDtoToString(CommentDto comment) {
        return "Id: %d, text: %s".formatted(
                comment.id(),
                comment.text());
    }
}
