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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "comments")
public class MongoComment {

    @Id
    private String id;

    private String text;

    @DBRef
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MongoBook mongoBook;

    @Transient
    private Long legacyId;

    public MongoComment(String text, MongoBook mongoBook) {
        this.id = null;
        this.text = text;
        this.mongoBook = mongoBook;
    }
}
