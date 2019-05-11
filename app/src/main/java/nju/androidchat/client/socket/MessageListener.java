package nju.androidchat.client.socket;

import nju.androidchat.shared.message.Message;

public interface MessageListener {
    void onMessageReceived(Message message);
}
