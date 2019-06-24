package nju.androidchat.client.mvp2;

import android.os.AsyncTask;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Setter;
import lombok.extern.java.Log;
import nju.androidchat.client.ClientMessage;
import nju.androidchat.client.socket.MessageListener;
import nju.androidchat.client.socket.SocketClient;
import nju.androidchat.shared.message.ClientSendMessage;
import nju.androidchat.shared.message.ErrorMessage;
import nju.androidchat.shared.message.Message;
import nju.androidchat.shared.message.RecallMessage;
import nju.androidchat.shared.message.RecallRequestMessage;
import nju.androidchat.shared.message.ServerSendMessage;

@Log
public class Mvp2TalkModel implements MessageListener, Mvp2Contract.TalkModel {

    private SocketClient client;

    @Setter
    private Mvp2Contract.TalkPresenter iMvp0TalkPresenter;

    public Mvp2TalkModel() {
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
    public void recallMessage(UUID messageId) {
        RecallRequestMessage recallRequestMessage = new RecallRequestMessage(messageId);
        AsyncTask.execute(() -> client.writeToServer(recallRequestMessage));
    }

    @Override
    public ClientMessage sendInformation(String message) {
        //处理事件
        LocalDateTime now = LocalDateTime.now();
        UUID uuid = UUID.randomUUID();

        String imageURL = getImageURL(message);

        System.out.println("!imageURL: " + imageURL);

        ClientMessage clientMessage = null;
        if (imageURL.equals("")) {
            clientMessage = new ClientMessage(uuid, now, getUsername(), message, false);
        } else {
            clientMessage = new ClientMessage(uuid, now, getUsername(), imageURL, true);
        }


        // 阻塞地把信息发送到服务器
        client.writeToServer(new ClientSendMessage(uuid, now, message));
        return clientMessage;
    }

    public static String getImageURL(String message) {
        String pattern = "!\\[.*\\]\\(.*\\)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(message);
        if (m.find()) {
            String imageString = m.group(0);
            String pattern2 = "\\(.*\\)";
            Pattern r2 = Pattern.compile(pattern2);
            Matcher m2 = r2.matcher(imageString);
            if (m2.find()) {
                String imageURL = m2.group(0);
                imageURL = imageURL.substring(1, imageURL.length() - 1);
                return imageURL;
            }
        }
        return "";
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
            String imageURL = getImageURL(serverSendMessage.getMessage());
            if (imageURL.equals("")) {
                iMvp0TalkPresenter.receiveMessage(new ClientMessage(serverSendMessage));
            } else {
                iMvp0TalkPresenter.receiveMessage(new ClientMessage(serverSendMessage.getMessageId(), serverSendMessage.getTime(), serverSendMessage.getSenderUsername(), imageURL, true));
            }

        } else if (message instanceof ErrorMessage) {
            // 接收到服务器的错误消息
            log.severe("Server error: " + ((ErrorMessage) message).getErrorMessage());
        } else if (message instanceof RecallMessage) {
            // 接受到服务器的撤回消息，MVC-0不实现
            iMvp0TalkPresenter.recallMessage(((RecallMessage) message).getMessageId());
        } else {
            // 不认识的消息
            log.severe("Unsupported message received: " + message.toString());
        }
    }
}
