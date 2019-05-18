package nju.androidchat.client.mvp2;

import java.util.List;

import lombok.AllArgsConstructor;
import nju.androidchat.client.ClientMessage;

@AllArgsConstructor
public class Mvp2TalkPresenter implements Mvp2Contract.Presenter {

    private Mvp2Contract.Model mvp0TalkModel;
    private Mvp2Contract.View iMvp0TalkView;

    private List<ClientMessage> clientMessages;

    @Override
    public void sendMessage(String content) {
        ClientMessage clientMessage = mvp0TalkModel.sendInformation(content);
        refreshMessageList(clientMessage);
    }

    @Override
    public void receiveMessage(ClientMessage clientMessage) {
        refreshMessageList(clientMessage);
    }

    @Override
    public String getUsername() {
        return mvp0TalkModel.getUsername();
    }

    private void refreshMessageList(ClientMessage clientMessage) {
        clientMessages.add(clientMessage);
        iMvp0TalkView.showMessageList(clientMessages);
    }

    //撤回消息，Mvp0暂不实现
    @Override
    public void recallMessage(int index0) {

    }

    @Override
    public void start() {

    }
}
