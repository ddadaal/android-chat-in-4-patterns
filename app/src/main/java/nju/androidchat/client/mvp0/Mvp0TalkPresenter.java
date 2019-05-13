package nju.androidchat.client.mvp0;

import java.util.List;

import lombok.AllArgsConstructor;
import nju.androidchat.client.ClientMessage;

@AllArgsConstructor
public class Mvp0TalkPresenter implements IMvp0TalkPresenter {

    private Mvp0TalkModel mvp0TalkModel;
    private IMvp0TalkView iMvp0TalkView;

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

    private void refreshMessageList(ClientMessage clientMessage) {
        clientMessages.add(clientMessage);
        iMvp0TalkView.showMessageList(clientMessages);
    }

    //撤回消息，Mvp0暂不实现
    @Override
    public void recallMessage(int index0) {

    }
}
