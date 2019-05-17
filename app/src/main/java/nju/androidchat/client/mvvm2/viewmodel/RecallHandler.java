package nju.androidchat.client.mvvm2.viewmodel;

import nju.androidchat.client.mvvm2.model.ClientMessageObservable;

public interface RecallHandler {
    void handleRecall(ClientMessageObservable messageObservable);
}
