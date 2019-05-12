package nju.androidchat.client.mvc0;

import android.os.AsyncTask;

import lombok.AllArgsConstructor;
import lombok.experimental.ExtensionMethod;
import nju.androidchat.client.Utils;

@AllArgsConstructor
public class Mvc0TalkController {

    private Mvc0TalkModel model;
    private Mvc0TalkActivity activity;

    // Controller将View传来的请求转发给Model进行处理
    public void sendInformation(String message) {
        model.sendInformation(message);
    }


    public void jumpBackToHome() {
        AsyncTask.execute(() -> {
            model.disconnect();
        });

        Utils.jumpToHome(activity);
    }
}
