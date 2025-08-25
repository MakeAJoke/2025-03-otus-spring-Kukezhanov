package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.projections.BookProjection;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "book", excerptProjection = BookProjection.class)
public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph("withAuthor")
    Optional<Book> findById(long id);

    @EntityGraph("withAuthor")
    List<Book> findAll();
}
