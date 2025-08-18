package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.News;
import ru.otus.hw.repository.NewsRepository;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class NewsPublisherServiceImpl implements NewsPublisherService {

    private final NewsRepository newsRepository;

    private final NewsPublisherGateway newsPublisherGateway;

    @Override
    public void startPublishingNews() {
        List<News> allNews = newsRepository.findAll();
        newsPublisherGateway.process(allNews);
    }
}
