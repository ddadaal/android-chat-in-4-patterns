package nju.androidchat.client;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nju.androidchat.shared.message.Message;
import nju.androidchat.shared.message.ServerSendMessage;

@AllArgsConstructor
public class ClientMessage extends Message {
    @Getter
    private UUID messageId;

    @Getter
    private LocalDateTime time;

    @Getter
    private String senderUsername;

    @Getter
    private String message;

    public ClientMessage(ServerSendMessage message) {
        this.messageId = message.getMessageId();
        this.time = message.getTime();
        this.senderUsername = message.getSenderUsername();
        this.message = message.getMessage();
    }
}