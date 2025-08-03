package ru.otus.hw.models.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.otus.hw.models.jpa.Comment;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "books")
public class MongoBook {

    @Id
    private String id;

    private String title;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MongoAuthor author;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<MongoGenre> genres;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @DBRef
    private List<Comment> comments;

    @Transient
    private Long legacyId;

    public MongoBook(String id) {
        this.id = id;
        this.title = null;
        this.author = null;
        this.genres = new ArrayList<>();
        this.comments = new ArrayList<>();
    }
}
