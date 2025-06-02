package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Optional<Book> findById(long id) {
        return Optional.ofNullable(entityManager.createQuery("""
                SELECT b FROM book b
                JOIN FETCH b.author
                WHERE b.id = :id
                """, Book.class).setParameter("id", id).getSingleResult());
    }

    @Override
    public List<Book> findAll() {
        return entityManager.createQuery("""
                SELECT b FROM book b
                JOIN FETCH b.author
                """, Book.class).getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            entityManager.persist(book);
            return book;
        }
        return entityManager.merge(book);
    }

    @Override
    public void deleteById(long id) {
        Book book = findById(id).orElseThrow(() ->
                new EntityNotFoundException("Book with id %d not found".formatted(id)));
        entityManager.remove(book);
    }
}
