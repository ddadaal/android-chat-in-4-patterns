package nju.androidchat.server.handlers;

import nju.androidchat.server.ConnectionHandler;
import nju.androidchat.shared.message.Message;

public interface MessageHandler<T extends Message> {
    void handle(T message, ConnectionHandler connectionHandler);
}
