package ru.otus.hw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.domain.News;

public interface NewsRepository extends JpaRepository<News, Long> {
}
