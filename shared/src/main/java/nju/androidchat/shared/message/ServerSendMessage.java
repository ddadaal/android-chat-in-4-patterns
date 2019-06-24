package nju.androidchat.shared.message;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class ServerSendMessage extends Message {
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

    public ServerSendMessage(UUID messageId, LocalDateTime time, String senderUsername, String message) {
        this.messageId = messageId;
        this.time = time;
        this.senderUsername = senderUsername;
        this.message = message;
        this.isImage = false;
    }
}
