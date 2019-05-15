package nju.androidchat.client.mvc1;

import android.os.AsyncTask;

import lombok.AllArgsConstructor;
import nju.androidchat.client.Utils;

@AllArgsConstructor
public class Mvc1TalkController {

    private Mvc1TalkModel model;
    private Mvc1TalkActivity activity;

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
