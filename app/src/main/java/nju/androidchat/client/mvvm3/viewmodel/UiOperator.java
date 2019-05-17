package nju.androidchat.client.mvvm3.viewmodel;

public interface UiOperator {
    void runOnUiThread(Runnable action);
    void scrollListToBottom();
    void sendBadWordNotice();
}
