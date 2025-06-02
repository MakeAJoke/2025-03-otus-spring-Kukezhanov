package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    private final CommentConverter commentConverter;

    @ShellMethod(value = "Find comment by id", key = "cid")
    public String findCommentById(long id) {
        return commentService.findById(id)
                .map(commentConverter::commentDtoToString)
                .orElse("Comment with id %d not found".formatted(id));
    }

    @ShellMethod(value = "Find comment by book id", key = "cbid")
    public String findCommentByBookId(long id) {
        return commentService.findAllByBookId(id).stream()
                .map(commentConverter::commentDtoToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Add comment to book", key = "cins")
    public String insertCommentForBook(long bookId, String text) {
        var commentDto = commentService.insert(bookId, text);
        return commentConverter.commentDtoToString(commentDto);
    }
}
