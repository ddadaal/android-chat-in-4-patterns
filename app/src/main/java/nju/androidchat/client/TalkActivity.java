package nju.androidchat.client;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import lombok.extern.java.Log;
import nju.androidchat.client.socket.MessageListener;
import nju.androidchat.client.socket.SocketClient;
import nju.androidchat.shared.message.ClientSendMessage;
import nju.androidchat.shared.message.Message;
import nju.androidchat.shared.message.RecallMessage;
import nju.androidchat.shared.message.ServerSendMessage;

@Log
public class TalkActivity extends AppCompatActivity implements MessageListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView editText = findViewById(R.id.et_content);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            //当actionId == XX_SEND 或者 XX_DONE时都触发
            //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
            //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
            if (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                hideKeyboard();
                //处理事件
                LocalDateTime now = LocalDateTime.now();
                UUID uuid = UUID.randomUUID();
                String message = editText.getText().toString();
                SocketClient.getClient().writeToServer(new ClientSendMessage(uuid, now, message));

                // 加到UI中
                addMessageToUi(new ClientMessage(uuid, now, SocketClient.getClient().getUsername(), message));
            }
            return false;
        });


        SocketClient.getClient().startListening(this);
    }

    // 往UI中加一条信息
    public void addMessageToUi(ClientMessage message) {
        // 加到UI中
    }

    @Override
    public void onBackPressed() {
        AsyncTask.execute(() -> {
            SocketClient.disconnectCurrent();
        });
        Intent intent = new Intent(TalkActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    // 点击空白位置 隐藏软键盘
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            return hideKeyboard();
        }
        return super.onTouchEvent(event);
    }

    //隐藏软键盘
    private boolean hideKeyboard() {
        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        return mInputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(this.getCurrentFocus()).getWindowToken(), 0);
    }

    @Override
    public void onMessageReceived(Message message) {
        if (message instanceof ServerSendMessage) {
            addMessageToUi(new ClientMessage((ServerSendMessage) message));
        } else if (message instanceof RecallMessage) {
            // 服务器要撤回消息
        }
    }
}
