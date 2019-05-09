package nju.androidchat.shared.message;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * 客户端到服务器：发送信息
 */
@AllArgsConstructor
public class ClientSendMessage extends Message {

    @Getter
    private LocalDateTime time;

    @Getter
    private String message;
}
