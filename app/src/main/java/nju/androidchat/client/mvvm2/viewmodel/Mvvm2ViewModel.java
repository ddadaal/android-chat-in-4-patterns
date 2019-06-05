package nju.androidchat.client.mvvm2.viewmodel;

import android.os.AsyncTask;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.extern.java.Log;
import nju.androidchat.client.BR;
import nju.androidchat.client.R;
import nju.androidchat.client.mvvm2.model.ClientMessageObservable;
import nju.androidchat.client.mvvm2.model.State;
import nju.androidchat.client.socket.MessageListener;
import nju.androidchat.client.socket.SocketClient;
import nju.androidchat.shared.message.ClientSendMessage;
import nju.androidchat.shared.message.ErrorMessage;
import nju.androidchat.shared.message.Message;
import nju.androidchat.shared.message.RecallMessage;
import nju.androidchat.shared.message.RecallRequestMessage;
import nju.androidchat.shared.message.ServerSendMessage;

@Log
public class Mvvm2ViewModel extends BaseObservable implements MessageListener, RecallHandler, LongClickListener {

    @Bindable
    @Getter
    private String messageToSend;
    @Getter
    private ObservableList<ClientMessageObservable> messageObservableList;
    @Getter
    private SocketClient client;

    private UiOperator uiOperator;

    public void setMessageToSend(String messageToSend) {
        this.messageToSend = messageToSend;
        notifyPropertyChanged(BR.messageToSend);
    }

    public Mvvm2ViewModel(UiOperator uiOperator) {
        this.uiOperator = uiOperator;
        messageToSend = "";
        messageObservableList = new ObservableArrayList<>();
        client = SocketClient.getClient();
        client.setMessageListener(this);
        client.startListening();
    }

    private void updateList(ClientMessageObservable clientMessage) {
        uiOperator.runOnUiThread(() -> {
            messageObservableList.add(clientMessage);
            uiOperator.scrollListToBottom();
        });
    }

    private void recallMessage(UUID uuid) {
        uiOperator.runOnUiThread(() -> {
            messageObservableList.stream()
                    .filter(message -> message.getMessageId().equals(uuid))
                    .findAny()
                    .ifPresent(message -> {
                        // 修改数据的状态即可
                        message.setState(State.WITHDRAWN);
                    });
        });
    }


    public void sendMessage() {
        LocalDateTime now = LocalDateTime.now();
        UUID uuid = UUID.randomUUID();
        String senderUsername = client.getUsername();
        ClientSendMessage clientSendMessage = new ClientSendMessage(uuid, now, messageToSend);
        ClientMessageObservable clientMessageObservable = new ClientMessageObservable(clientSendMessage, senderUsername);
        updateList(clientMessageObservable);

        AsyncTask.execute(() -> client.writeToServer(clientSendMessage));
    }

    private void sendRecallRequest(UUID uuid) {
        RecallRequestMessage recallRequestMessage = new RecallRequestMessage(uuid);
        AsyncTask.execute(() -> client.writeToServer(recallRequestMessage));
    }

    @Override
    public void onMessageReceived(Message message) {
        if (message instanceof ServerSendMessage) {
            // 接受到其他设备发来的消息
            // 增加到自己的消息列表里，并通知UI修改
            ServerSendMessage serverSendMessage = (ServerSendMessage) message;
            log.info(String.format("%s sent a messageToSend: %s",
                    serverSendMessage.getSenderUsername(),
                    serverSendMessage.getMessage()
            ));
            ClientMessageObservable clientMessage = new ClientMessageObservable(serverSendMessage);
            updateList(clientMessage);
        } else if (message instanceof ErrorMessage) {
            // 接收到服务器的错误消息
            log.severe("Server error: " + ((ErrorMessage) message).getErrorMessage());

        } else if (message instanceof RecallMessage) {
            RecallMessage recallMessage = (RecallMessage) message;
            UUID uuid = recallMessage.getMessageId();
            log.info(String.format("A recallMessage received: %s",
                    uuid
            ));
            recallMessage(uuid);
        } else {
            // 不认识的消息
            log.severe("Unsupported messageToSend received: " + message.toString());

        }
    }

    @Override
    public boolean onLongClick(ClientMessageObservable messageObservable) {
        uiOperator.showRecallUi(messageObservable, this);
        return true;
    }

    @Override
    public void handleRecall(ClientMessageObservable messageObservable) {
        messageObservable.setState(State.WITHDRAWN);
        UUID uuid = messageObservable.getMessageId();
        sendRecallRequest(uuid);
    }

    public void disconnect() {
        AsyncTask.execute(() -> client.disconnect());
    }
}
