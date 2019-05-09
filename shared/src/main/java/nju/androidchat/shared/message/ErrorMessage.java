package nju.androidchat.shared.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务器到客户端：发生错误
 */
@AllArgsConstructor
public class ErrorMessage extends Message {
    @Getter
    private String errorMessage;
}
