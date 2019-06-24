package nju.androidchat.client.hw1;

import java.util.List;
import java.util.UUID;

import nju.androidchat.client.ClientMessage;

public interface Mvp2Contract {
    interface TalkView extends BaseView<TalkPresenter> {
        void showMessageList(List<ClientMessage> messages);
    }

    interface TalkPresenter extends BasePresenter {
        void sendMessage(String content);

        void receiveMessage(ClientMessage content);

        String getUsername();

        void recallMessage(UUID messageId);
    }

    interface TalkModel {
        ClientMessage sendInformation(String message);

        String getUsername();

        void recallMessage(UUID messageId);
    }
}
