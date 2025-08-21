package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.otus.hw.services.NewsPublisherService;

@RequiredArgsConstructor
@ConditionalOnProperty(
        value = "app.runner.enabled",
        havingValue = "true",
        matchIfMissing = true
)
@Component
public class AppRunner implements CommandLineRunner {

    private final NewsPublisherService newsPublisherService;

    @Override
    public void run(String... args) throws Exception {
        newsPublisherService.startPublishingNews();
    }
}
