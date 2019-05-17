package nju.androidchat.client.mvvm2.viewmodel;


import nju.androidchat.client.mvvm2.model.ClientMessageObservable;

public interface UiOperator{
    void runOnUiThread(Runnable action);
    void scrollListToBottom();
    void showRecallUi(ClientMessageObservable messageObservable, RecallHandler recallHandler);
}
