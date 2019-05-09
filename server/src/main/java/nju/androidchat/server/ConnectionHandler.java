package nju.androidchat.server;

import nju.androidchat.shared.message.Message;

public interface ConnectionHandler {
    void sendToAllOtherClients(Message message);

    void setTerminate(boolean terminate);

    String getUsername();

    void log(String content);
}
