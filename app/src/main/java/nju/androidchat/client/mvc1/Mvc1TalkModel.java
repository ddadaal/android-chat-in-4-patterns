package nju.androidchat.client.mvc1;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.extern.java.Log;
import nju.androidchat.client.ClientMessage;
import nju.androidchat.client.socket.MessageListener;
import nju.androidchat.client.socket.SocketClient;
import nju.androidchat.shared.message.ClientSendMessage;
import nju.androidchat.shared.message.ErrorMessage;
import nju.androidchat.shared.message.Message;
import nju.androidchat.shared.message.RecallMessage;
import nju.androidchat.shared.message.ServerSendMessage;

@Log
public class Mvc1TalkModel implements MessageListener {

    // Model需要Activity注册的监听器
    public interface MessageListUpdateListener {
        void onListUpdate(List<ClientMessage> messages);
    }

    private SocketClient client;
    private MessageListUpdateListener listener;
    private List<ClientMessage> messageList;

    public Mvc1TalkModel() {
        this.client = SocketClient.getClient();
        this.messageList = new ArrayList<>();

        // Model本身去注册Socket的消息接受事件
        client.setMessageListener(this);
    }

    // 处理从服务器接收到的消息
    @Override
    public void onMessageReceived(Message message) {
        if (message instanceof ServerSendMessage) {
            // 接受到其他设备发来的消息
            // 增加到自己的消息列表里，并通知UI修改
            ServerSendMessage serverSendMessage = (ServerSendMessage) message;
            log.info(String.format("%s sent a message: %s",
                    serverSendMessage.getSenderUsername(),
                    serverSendMessage.getMessage()
            ));
            messageList.add(new ClientMessage(serverSendMessage));
            if (listener != null) {
                listener.onListUpdate(messageList);
            }
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

    public String getUsername() {
        return client.getUsername();
    }

    public void setMessageListener(MessageListUpdateListener listener) {
        this.listener = listener;
    }

    public void startListening() {
        client.startListening();
    }

    public void disconnect() {
        client.disconnect();
    }

    // 接受Controller的Delegate，实际进行处理
    public void sendInformation(String message) {
        //处理事件
        LocalDateTime now = LocalDateTime.now();
        UUID uuid = UUID.randomUUID();

        // 可选：乐观更新，发送信息之前就增加到自己的消息列表里，并通知View更新UI
        messageList.add(new ClientMessage(uuid, now, getUsername(), message));
        if (listener != null) {
            listener.onListUpdate(messageList);
        }

        // 阻塞地把信息发送到服务器
        client.writeToServer(new ClientSendMessage(uuid, now, message));
    }
}
