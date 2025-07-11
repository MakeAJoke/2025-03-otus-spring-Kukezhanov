package ru.otus.hw.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.dto.GenreDto;

@Component
public class GenreConverter implements Converter<String, GenreDto> {
    public String genreToString(Genre genre) {
        return "Id: %d, Name: %s".formatted(genre.getId(), genre.getName());
    }

    public GenreDto genreToDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }

    public String genreDtoToString(GenreDto genre) {
        return "Id: %d, Name: %s".formatted(genre.id(), genre.name());
    }

    @Override
    public GenreDto convert(String id) {
        return new GenreDto(Integer.valueOf(id), null);
    }
}
