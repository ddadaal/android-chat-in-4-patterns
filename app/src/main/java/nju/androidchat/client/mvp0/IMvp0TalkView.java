package nju.androidchat.client.mvp0;

import java.util.List;

import nju.androidchat.client.ClientMessage;

public interface IMvp0TalkView {

    void showMessageList(List<ClientMessage> messages);
}
