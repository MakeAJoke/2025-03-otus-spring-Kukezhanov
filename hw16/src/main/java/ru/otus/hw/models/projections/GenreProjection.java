package ru.otus.hw.models.projections;

import org.springframework.data.rest.core.config.Projection;
import ru.otus.hw.models.Genre;

@Projection(name = "genreProjection", types = Genre.class)
public interface GenreProjection {
    Long getId();

    String getName();
}
