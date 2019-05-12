package nju.androidchat.shared.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 服务器到客户端：发生错误
 */
@ToString
@AllArgsConstructor
public class ErrorMessage extends Message {
    @Getter
    private String errorMessage;
}
