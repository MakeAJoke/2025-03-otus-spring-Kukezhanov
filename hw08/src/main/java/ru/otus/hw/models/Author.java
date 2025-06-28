package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "authors")
public class Author {

    @Id
    private String id;

    private String fullName;

    @DBRef
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Book> books;

    public Author(String fullName) {
        this.id = null;
        this.fullName = fullName;
        this.books = new ArrayList<>();
    }
}
