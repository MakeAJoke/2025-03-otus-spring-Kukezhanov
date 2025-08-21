package ru.otus.hw.services;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.domain.News;

import java.util.List;

@MessagingGateway
public interface NewsPublisherGateway {

    @Gateway(requestChannel = "dbInputChannel", replyChannel = "publisherChannel")
    List<News> process(List<News> news);
}
