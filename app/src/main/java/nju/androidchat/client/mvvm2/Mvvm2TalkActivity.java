package nju.androidchat.client.mvvm2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.extern.java.Log;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.socket.MessageListener;
import nju.androidchat.client.socket.SocketClient;
import nju.androidchat.shared.message.ClientSendMessage;
import nju.androidchat.shared.message.ErrorMessage;
import nju.androidchat.shared.message.Message;
import nju.androidchat.shared.message.RecallMessage;
import nju.androidchat.shared.message.ServerSendMessage;

@Log
public class Mvvm2TalkActivity extends AppCompatActivity implements MessageListener, TextView.OnEditorActionListener {

    private List<ClientMessageObservable> messageList = new ArrayList<>();
    private SocketClient client = SocketClient.getClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mvvm);
        ListView listView = findViewById(R.id.chat_content);
        Mvvm2ListAdapter adapter = new Mvvm2ListAdapter(getLayoutInflater(), messageList);
        listView.setAdapter(adapter);
        // Input事件处理
        EditText editText = findViewById(R.id.et_content);
        editText.setOnEditorActionListener(this);

        // 注册自己
        client.setMessageListener(this);

        client.startListening();

//        // 测试用
//        initData();
    }


    private void initData(){
        for(int i = 0; i < 10; i++){
            String message = "This is the "+i+" message!";
            LocalDateTime now = LocalDateTime.now();
            UUID uuid = UUID.randomUUID();
            String senderUsername = "Somebody";
            ClientSendMessage clientSendMessage = new ClientSendMessage(uuid, now, message);
            messageList.add(new ClientMessageObservable(clientSendMessage, senderUsername));
        }
    }

    @Override
    public void onBackPressed() {
        AsyncTask.execute(() -> {
            client.disconnect();
        });

        Utils.jumpToHome(this);
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
        String message = text.getText().toString();
        LocalDateTime now = LocalDateTime.now();
        UUID uuid = UUID.randomUUID();
        String senderUsername = client.getUsername();
        ClientSendMessage clientSendMessage = new ClientSendMessage(uuid, now, message);
        messageList.add(new ClientMessageObservable(clientSendMessage, senderUsername));
        AsyncTask.execute(() -> {
            client.writeToServer(clientSendMessage);
        });
    }

    public void onBtnSendClicked(View v) {
        hideKeyboard();
        sendText();
    }

    @Override
    public void onMessageReceived(Message message) {
        if (message instanceof ServerSendMessage) {
            // 接受到其他设备发来的消息
            // 增加到自己的消息列表里，并通知UI修改
            ServerSendMessage serverSendMessage = (ServerSendMessage) message;
            log.info(String.format("%s sent a message: %s",
                    serverSendMessage.getSenderUsername(),
                    serverSendMessage.getMessage()
            ));
            messageList.add(new ClientMessageObservable(serverSendMessage));
        } else if (message instanceof ErrorMessage) {
            // 接收到服务器的错误消息
            log.severe("Server error: " + ((ErrorMessage) message).getErrorMessage());

        } else if (message instanceof RecallMessage) {
            // 接受到服务器的撤回消息，MVVM-0不实现
        } else {
            // 不认识的消息
            log.severe("Unsupported message received: " + message.toString());

        }
    }
}
