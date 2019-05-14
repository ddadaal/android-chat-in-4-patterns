package nju.androidchat.client.mvp0;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Setter;
import lombok.extern.java.Log;
import nju.androidchat.client.ClientMessage;
import nju.androidchat.client.mvc0.Mvc0TalkModel;
import nju.androidchat.client.socket.MessageListener;
import nju.androidchat.client.socket.SocketClient;
import nju.androidchat.shared.message.ClientSendMessage;
import nju.androidchat.shared.message.ErrorMessage;
import nju.androidchat.shared.message.Message;
import nju.androidchat.shared.message.RecallMessage;
import nju.androidchat.shared.message.ServerSendMessage;

@Log
public class Mvp0TalkModel implements MessageListener, Mvp0Contract.Model {

    private SocketClient client;

    @Setter
    private Mvp0Contract.Presenter iMvp0TalkPresenter;

    public Mvp0TalkModel() {
        this.client = SocketClient.getClient();
        // Model本身去注册Socket的消息接受事件
        client.setMessageListener(this);
        client.startListening();
    }

    @Override
    public String getUsername() {
        return client.getUsername();
    }

    @Override
    public ClientMessage sendInformation(String message) {
        //处理事件
        LocalDateTime now = LocalDateTime.now();
        UUID uuid = UUID.randomUUID();
        ClientMessage clientMessage = new ClientMessage(uuid, now, getUsername(), message);
        // 阻塞地把信息发送到服务器
        client.writeToServer(new ClientSendMessage(uuid, now, message));
        return clientMessage;
    }

    @Override
    public void onMessageReceived(Message message) {
        if (message instanceof ServerSendMessage) {
            // 接受到其他设备发来的消息
            ServerSendMessage serverSendMessage = (ServerSendMessage) message;
            log.info(String.format("%s sent a message: %s",
                    serverSendMessage.getSenderUsername(),
                    serverSendMessage.getMessage()
            ));
            iMvp0TalkPresenter.receiveMessage(new ClientMessage(serverSendMessage));
        } else if (message instanceof ErrorMessage) {
            // 接收到服务器的错误消息
            log.severe("Server error: " + ((ErrorMessage) message).getErrorMessage());
        } else if (message instanceof RecallMessage) {
            // 接受到服务器的撤回消息，MVC-0不实现
        } else {
            // 不认识的消息
            log.severe("Unsupported message received: " + message.toString());
        }
    }
}
