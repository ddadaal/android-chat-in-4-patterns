package nju.androidchat.shared.message;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 客户端到服务器：发送信息
 */
@ToString
@AllArgsConstructor
public class ClientSendMessage extends Message {

    @Getter
    private UUID messageId;

    @Getter
    private LocalDateTime time;

    @Getter
    private String message;
}
