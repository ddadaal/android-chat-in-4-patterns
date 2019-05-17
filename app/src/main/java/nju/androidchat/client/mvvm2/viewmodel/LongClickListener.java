package nju.androidchat.client.mvvm2.viewmodel;

import nju.androidchat.client.mvvm2.model.ClientMessageObservable;

public interface LongClickListener {
    boolean onLongClick(ClientMessageObservable messageObservable);
}
