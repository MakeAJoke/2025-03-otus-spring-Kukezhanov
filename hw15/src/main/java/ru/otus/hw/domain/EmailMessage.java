package ru.otus.hw.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailMessage implements Message {

    private final Long id;

    private final String subject;

    private final String text;

    private final String sign;
}
