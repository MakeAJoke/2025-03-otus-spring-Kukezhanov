package ru.otus.hw.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class TelegramMessage implements Message {

    private final Long id;

    private final String text;

    private final List<String> tags;

    public String getTags() {
        return tags.stream().map(tag -> "#" + tag).collect(Collectors.joining(", "));
    }
}
