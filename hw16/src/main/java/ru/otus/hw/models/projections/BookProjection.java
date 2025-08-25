package ru.otus.hw.models.projections;

import org.springframework.data.rest.core.config.Projection;
import ru.otus.hw.models.Book;

import java.util.List;

@Projection(name = "withAuthorAndGenres", types = Book.class)
public interface BookProjection {
    Long getId();

    String getTitle();

    AuthorProjection getAuthor();

    List<GenreProjection> getGenres();
}
