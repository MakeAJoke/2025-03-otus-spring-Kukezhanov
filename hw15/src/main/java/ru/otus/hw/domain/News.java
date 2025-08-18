package ru.otus.hw.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@Table(name = "news")
@AllArgsConstructor
@NoArgsConstructor
public class News {

    @Id
    private Long id;

    @Column
    private String type;

    @Column
    private String text;

    @Column(name = "created_at")
    private Date createdAt;

}
