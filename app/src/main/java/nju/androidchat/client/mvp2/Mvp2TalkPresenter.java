package nju.androidchat.client.mvp2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import nju.androidchat.client.ClientMessage;

@AllArgsConstructor
public class Mvp2TalkPresenter implements Mvp2Contract.TalkPresenter {

    private Mvp2Contract.TalkModel mvp2TalkModel;
    private Mvp2Contract.TalkView iMvp2TalkView;

    private List<ClientMessage> clientMessages;

    @Override
    public void sendMessage(String content) {
        ClientMessage clientMessage = mvp2TalkModel.sendInformation(content);
        refreshMessageList(clientMessage);
    }

    @Override
    public void receiveMessage(ClientMessage clientMessage) {
        refreshMessageList(clientMessage);
    }

    @Override
    public String getUsername() {
        return mvp2TalkModel.getUsername();
    }

    private void refreshMessageList(ClientMessage clientMessage) {
        clientMessages.add(clientMessage);
        iMvp2TalkView.showMessageList(clientMessages);
    }

    //撤回消息，Mvp0暂不实现
    @Override
    public void recallMessage(UUID messageId) {
        // 操作界面
        List<ClientMessage> newMessages = new ArrayList<>();
        for (ClientMessage clientMessage : clientMessages) {
            if (clientMessage.getMessageId().equals(messageId)) {
                newMessages.add(new ClientMessage(clientMessage.getMessageId(), clientMessage.getTime(), clientMessage.getSenderUsername(), "(已撤回)"));
            } else {
                newMessages.add(clientMessage);
            }
        }
        this.clientMessages = newMessages;
        this.iMvp2TalkView.showMessageList(newMessages);

        // 操作数据
        this.mvp2TalkModel.recallMessage(messageId);

    }

    @Override
    public void start() {

    }
}
