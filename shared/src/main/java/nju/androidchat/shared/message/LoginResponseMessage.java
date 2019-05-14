package nju.androidchat.shared.message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 服务器到客户端：登录响应
 * 登录写法：并不是像HTTP一样一个request一个response！
 * 客户端发送流和响应流是分开的！
 * 所以在登录按钮按下前，就初始化客户端的SocketClient建立Socket连接
 * 然后在这个连接上调用writeToServer，发出LoginRequestMessage信息
 * 然后当SocketClient接受到LoginResponseMessage信息时说明登录成功
 */
@ToString
@AllArgsConstructor
public class LoginResponseMessage extends Message {
    @Getter
    private String loggedInUsername;
}
