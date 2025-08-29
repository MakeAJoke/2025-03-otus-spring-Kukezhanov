package ru.otus.hw.models.projections;

import org.springframework.data.rest.core.config.Projection;
import ru.otus.hw.models.Comment;

@Projection(name = "commentProjection", types = Comment.class)
public interface CommentProjection {
    String getText();
}
