package nju.androidchat.client.mvp0;

import nju.androidchat.client.ClientMessage;

public interface IMvp0TalkPresenter {

    void sendMessage(String content);

    void receiveMessage(ClientMessage content);

    //撤回消息mvp0不实现
    void recallMessage(int index0);

}
