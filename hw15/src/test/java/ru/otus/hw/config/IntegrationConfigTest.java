package ru.otus.hw.config;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.domain.EmailMessage;
import ru.otus.hw.domain.TelegramMessage;
import ru.otus.hw.services.EmailMessageSender;
import ru.otus.hw.services.NewsPublisherServiceImpl;
import ru.otus.hw.services.TelegramMessageSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = "app.runner.enabled=false")
@SpringIntegrationTest
public class IntegrationConfigTest {

    @Autowired
    NewsPublisherServiceImpl newsPublisherService;

    @MockitoBean
    TelegramMessageSender telegramSender;

    @MockitoBean
    EmailMessageSender emailSender;

    @Test
    void testPublishingThroughGateway() {

        newsPublisherService.startPublishingNews();

        ArgumentCaptor<TelegramMessage> telegramMessageArgumentCaptor = ArgumentCaptor.forClass(TelegramMessage.class);
        ArgumentCaptor<EmailMessage> emailMessageArgumentCaptor = ArgumentCaptor.forClass(EmailMessage.class);

        verify(telegramSender, timeout(5000).times(3)).send(telegramMessageArgumentCaptor.capture());
        verify(emailSender, timeout(5000).times(3)).send(emailMessageArgumentCaptor.capture());

        assertThat(telegramMessageArgumentCaptor.getAllValues())
                .hasSize(3)
                .extracting(TelegramMessage::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L);

        assertThat(emailMessageArgumentCaptor.getAllValues())
                .hasSize(3)
                .extracting(EmailMessage::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L);
    }
}
