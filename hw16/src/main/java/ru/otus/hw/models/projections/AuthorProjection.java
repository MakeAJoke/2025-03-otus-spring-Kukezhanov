package ru.otus.hw.models.projections;

import org.springframework.data.rest.core.config.Projection;
import ru.otus.hw.models.Author;

@Projection(name = "authorProjection", types = Author.class)
public interface AuthorProjection {
    Long getId();

    String getFullName();
}
