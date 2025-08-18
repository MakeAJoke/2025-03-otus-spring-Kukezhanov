package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannelSpec;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;
import ru.otus.hw.domain.EmailMessage;
import ru.otus.hw.domain.News;
import ru.otus.hw.domain.TelegramMessage;
import ru.otus.hw.services.EmailMessageSender;
import ru.otus.hw.services.TelegramMessageSender;

import java.util.List;
import java.util.concurrent.Executors;

@Configuration
public class IntegrationConfig {

    @Bean
    public MessageChannel dbInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel publisherChannel() {
        return new PublishSubscribeChannel(Executors.newCachedThreadPool());
    }

    @Bean
    public MessageChannelSpec<?, ?> toTelegram() {
        return MessageChannels.queue(10);
    }

    @Bean
    public MessageChannelSpec<?, ?> toEmails() {
        return MessageChannels.queue(10);
    }

    @Bean
    public IntegrationFlow newsIntegrationFrom() {
        return IntegrationFlow.from(dbInputChannel())
                .split()
                .channel(publisherChannel())
                .get();
    }

    @Bean
    public IntegrationFlow telegramNewsFlow() {
        return IntegrationFlow.from(publisherChannel())
                .<News, TelegramMessage>transform(news ->
                        new TelegramMessage(news.getId(), news.getText(), List.of(news.getType()))
                )
                .channel(toTelegram())
                .get();
    }

    @Bean
    public IntegrationFlow emailNewsFlow() {
        return IntegrationFlow.from(publisherChannel())
                .<News, EmailMessage>transform(news ->
                        new EmailMessage(
                                news.getId(),
                                "News about: " + news.getType(),
                                news.getText(),
                                "Best regards, NewsPapa")
                )
                .channel(toEmails())
                .get();
    }

    @Bean
    public IntegrationFlow toTelegramSenderFlow(TelegramMessageSender telegramMessageSender) {
        return IntegrationFlow.from(toTelegram())
                .handle(telegramMessageSender, "send")
                .get();
    }


    @Bean
    public IntegrationFlow toEmailSenderFlow(EmailMessageSender emailMessageSender) {
        return IntegrationFlow.from(toEmails())
                .handle(emailMessageSender, "send")
                .get();
    }


}
