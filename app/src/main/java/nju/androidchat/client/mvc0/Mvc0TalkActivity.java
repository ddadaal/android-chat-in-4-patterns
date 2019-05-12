package nju.androidchat.client.mvc0;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.extern.java.Log;
import nju.androidchat.client.ClientMessage;
import nju.androidchat.client.MainActivity;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.socket.MessageListener;
import nju.androidchat.client.socket.SocketClient;
import nju.androidchat.shared.message.ClientSendMessage;
import nju.androidchat.shared.message.Message;
import nju.androidchat.shared.message.RecallMessage;
import nju.androidchat.shared.message.ServerSendMessage;

@Log
public class Mvc0TalkActivity extends AppCompatActivity implements Mvc0TalkModel.MessageListUpdateListener, TextView.OnEditorActionListener {

    private Mvc0TalkModel model = new Mvc0TalkModel();
    private Mvc0TalkController controller = new Mvc0TalkController(model, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Input事件处理
        TextView editText = findViewById(R.id.et_content);
        editText.setOnEditorActionListener(this);

        // View向Model注册事件并开始监听
        model.setMessageListener(this);
        model.startListening();
    }

    // 处理Model更新事件，更新UI
    @Override
    public void onListUpdate(List<ClientMessage> messages) {
        log.info("UI Update called");
    }

    @Override
    public void onBackPressed() {
        controller.jumpBackToHome();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            return hideKeyboard();
        }
        return super.onTouchEvent(event);
    }

    private boolean hideKeyboard() {
        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        return mInputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(this.getCurrentFocus()).getWindowToken(), 0);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND
                || actionId == EditorInfo.IME_ACTION_DONE
                || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
            hideKeyboard();

            // 异步地让Controller处理事件
            AsyncTask.execute(() -> {
                controller.sendInformation(v.getText().toString());
            });
        }
        return false;
    }


}
