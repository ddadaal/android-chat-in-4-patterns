package nju.androidchat.client.mvp1;

import java.time.LocalDateTime;
import java.util.List;

import nju.androidchat.client.ClientMessage;

public interface Mvp1Contract {
    interface TalkView extends BaseView<TalkPresenter> {
        void showMessageList(List<ClientMessage> messages);
    }

    interface TalkPresenter extends BasePresenter {
        void sendMessage(String content);

        void receiveMessage(ClientMessage content);

        String getUsername();

        //撤回消息mvp0不实现
        void recallMessage(int index0);
    }

    interface TalkModel {
        ClientMessage sendInformation(String message);

        String getUsername();
    }

    interface BackupView extends BaseView<BackupPresenter> {
        void editBtnStatusAndText(boolean canEdit, String text);

        void editTextView(String text);
    }

    interface BackupPresenter extends BasePresenter {
        void backup();
    }

    interface BackupModel {
        void backup();

        LocalDateTime getLastUpdated();
    }
}
