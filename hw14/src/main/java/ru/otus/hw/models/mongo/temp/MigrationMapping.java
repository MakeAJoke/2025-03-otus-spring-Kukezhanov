package ru.otus.hw.models.mongo.temp;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "migration_mapping")
public class MigrationMapping {

    @Id
    private String id;

    private String entity;

    private Long legacyId;

    private String mongoId;
}
