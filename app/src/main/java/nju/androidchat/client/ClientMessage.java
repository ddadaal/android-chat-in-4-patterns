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

    @Getter
    private boolean isImage;

    public ClientMessage(ServerSendMessage message) {
        this.messageId = message.getMessageId();
        this.time = message.getTime();
        this.senderUsername = message.getSenderUsername();
        this.message = message.getMessage();
        this.isImage = message.isImage();
    }

    public ClientMessage(UUID uuid, LocalDateTime time, String senderUsername, String message) {
        this.messageId = uuid;
        this.time = time;
        this.senderUsername = senderUsername;
        this.message = message;
        this.isImage = false;
    }
}