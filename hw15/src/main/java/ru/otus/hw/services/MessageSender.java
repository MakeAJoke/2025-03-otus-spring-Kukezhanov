package ru.otus.hw.services;

import ru.otus.hw.domain.Message;

public interface MessageSender {

    public void send(Message message);
}
