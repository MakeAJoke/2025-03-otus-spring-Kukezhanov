package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.projections.CommentProjection;

import java.util.List;

@RepositoryRestResource(path = "comment", excerptProjection = CommentProjection.class)
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @RestResource(path = "byBook", rel = "byBook")
    List<Comment> findAllByBookId(@Param("bookId") long bookId);
}
