package nju.androidchat.client.mvp2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.extern.java.Log;
import nju.androidchat.client.ClientMessage;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.component.ItemImgReceive;
import nju.androidchat.client.component.ItemImgSend;
import nju.androidchat.client.component.ItemTextReceive;
import nju.androidchat.client.component.ItemTextSend;
import nju.androidchat.client.component.OnRecallMessageRequested;

@Log
public class Mvp2TalkActivity extends AppCompatActivity implements Mvp2Contract.TalkView, TextView.OnEditorActionListener, OnRecallMessageRequested {
    private Mvp2Contract.TalkPresenter presenter;
    private final static String pattern = "!\\[.*?\\]\\(.*?\\)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Mvp2TalkModel mvp2TalkModel = new Mvp2TalkModel();

        // Create the presenter
        this.presenter = new Mvp2TalkPresenter(mvp2TalkModel, this, new ArrayList<>());
        mvp2TalkModel.setIMvp0TalkPresenter(this.presenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void showMessageList(List<ClientMessage> messages) {
        runOnUiThread(() -> {
                    LinearLayout content = findViewById(R.id.chat_content);

                    // 删除所有已有的ItemText
                    content.removeAllViews();

                    // 增加ItemText
                    for (ClientMessage message : messages) {
                        String text = String.format("%s", message.getMessage());
                        // 如果是自己发的，增加ItemTextSend
                        text = text.trim();
                        if (text.matches(pattern)) {
                            String imgURL = text.substring(text.indexOf('(') + 1, text.length() - 1);
                            if (message.getSenderUsername().equals(this.presenter.getUsername())) {
                                content.addView(new ItemImgSend(this, imgURL, message.getMessageId(), this));
//                              content.addView(new ItemImgSend(this,"http://news.sciencenet.cn/upload/news/images/2011/3/20113301123128272.jpg",message.getMessageId(),this));

                            } else {
                                content.addView(new ItemImgReceive(this, imgURL, message.getMessageId()));
                            }
                        } else {
                            if (message.getSenderUsername().equals(this.presenter.getUsername())) {
                                content.addView(new ItemTextSend(this, text, message.getMessageId(), this));

                            } else {
                                content.addView(new ItemTextReceive(this, text, message.getMessageId()));

                            }
                        }

                    }

                    Utils.scrollListToBottom(this);
                }
        );
    }

    @Override
    public void setPresenter(Mvp2Contract.TalkPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            return hideKeyboard();
        }
        return super.onTouchEvent(event);
    }

    private boolean hideKeyboard() {
        return Utils.hideKeyboard(this);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (Utils.send(actionId, event)) {
            hideKeyboard();
            // 异步地让Controller处理事件
            sendText();
        }
        return false;
    }

    private void sendText() {
        EditText text = findViewById(R.id.et_content);
        AsyncTask.execute(() -> {
            this.presenter.sendMessage(text.getText().toString());
        });
        text.setText("");
    }

    public void onBtnSendClicked(View v) {
        hideKeyboard();
        sendText();
    }

    // 当用户长按消息，并选择撤回消息时做什么
    @Override
    public void onRecallMessageRequested(UUID messageId) {
        this.presenter.recallMessage(messageId);
    }

}
