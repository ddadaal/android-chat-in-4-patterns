package nju.androidchat.client.mvvm0.viewmodel;

public interface UiOperator {
    void runOnUiThread(Runnable action);
    void scrollListToBottom();
}
