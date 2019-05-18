package nju.androidchat.client.mvp1;

import java.util.List;

import lombok.AllArgsConstructor;
import nju.androidchat.client.ClientMessage;

@AllArgsConstructor
public class Mvp1TalkPresenter implements Mvp1Contract.TalkPresenter {

    private Mvp1Contract.TalkModel mvp0TalkModel;
    private Mvp1Contract.TalkView iMvp0TalkView;

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

    //撤回消息，Mvp1暂不实现
    @Override
    public void recallMessage(int index0) {

    }

    @Override
    public void start() {

    }
}
