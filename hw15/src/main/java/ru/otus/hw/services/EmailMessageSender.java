package ru.otus.hw.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Message;

@Slf4j
@Service
public class EmailMessageSender implements MessageSender {

    @Override
    public void send(Message message) {
        log.info("send message: " + message.toString() + " to email");
    }
}
