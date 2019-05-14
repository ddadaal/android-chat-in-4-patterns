package nju.androidchat.client.frp0;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jakewharton.rxbinding3.view.RxView;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Future;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import nju.androidchat.client.ClientMessage;
import nju.androidchat.client.R;
import nju.androidchat.client.component.ItemTextReceive;
import nju.androidchat.client.component.ItemTextSend;
import nju.androidchat.client.socket.SocketClient;
import nju.androidchat.shared.message.ClientSendMessage;
import nju.androidchat.shared.message.ErrorMessage;
import nju.androidchat.shared.message.Message;
import nju.androidchat.shared.message.RecallMessage;
import nju.androidchat.shared.message.ServerSendMessage;

@lombok.extern.java.Log
public class Frp0TalkActivity extends AppCompatActivity {

    private SocketClient socketClient;
    private Observable<ClientSendMessage> sendMessages$ = Observable.empty();
    private Observable<Message> receiveMessage$ = Observable.empty();
    private Observable<LinearLayout> addToViewMessages$ = Observable.empty();

    Button sendButton;
    EditText editText;
    LinearLayout messageList;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 socket
        socketClient = SocketClient.getClient();


        // 初始控件
        sendButton = findViewById(R.id.send_btn);
        editText = findViewById(R.id.et_content);
        messageList = findViewById(R.id.chat_content);

        // 初始流
        this.receiveMessage$ = this.createReceiveMessageStream();
        this.sendMessages$ = this.createSendMessageStream();


        // 订阅流
        this.sendMessages$
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(message -> {
                    editText.setText("");
                    messageList.addView(new ItemTextSend(this, message.getMessage()));
                })
                .observeOn(Schedulers.io()) // 发送消息网络要在 io线程做
                .subscribe(clientSendMessage -> {
                    Log.d("send", clientSendMessage.toString());
                    this.socketClient.writeToServer(clientSendMessage);
                }, Throwable::printStackTrace);

        this.receiveMessage$
                .observeOn(AndroidSchedulers.mainThread())
                .filter(message -> message instanceof ServerSendMessage)
                .subscribe(message -> {
                    Log.d("receive", message.toString());
                    if (message instanceof ServerSendMessage) {
                        // 接受到其他设备发来的消息
                        // 增加到自己的消息列表里，并通知UI修改
                        ServerSendMessage serverSendMessage = (ServerSendMessage) message;
                        log.info(String.format("%s sent a message: %s",
                                serverSendMessage.getSenderUsername(),
                                serverSendMessage.getMessage()
                        ));
                        messageList.addView(new ItemTextReceive(this, ((ServerSendMessage)message).getMessage()));
                    } else if (message instanceof ErrorMessage) {
                        // 接收到服务器的错误消息
                        log.severe("Server error: " + ((ErrorMessage) message).getErrorMessage());
                    } else if (message instanceof RecallMessage) {
                        // 接受到服务器的撤回消息，FRP-0不实现
                    } else {
                        // 不认识的消息
                        log.severe("Unsupported message received: " + message.toString());
                    }

                }, Throwable::printStackTrace);

    }

    /**
     * 发送出去的消息流
     */
    @SuppressLint("CheckResult")
    private Observable<ClientSendMessage> createSendMessageStream() {
        // 发送按钮事件
        Observable<Unit> clicks$ = RxView.clicks(sendButton);

        return clicks$
                .observeOn(AndroidSchedulers.mainThread())
                .map(e -> {
                    UUID uuid = UUID.randomUUID();
                    LocalDateTime now = LocalDateTime.now();
                    String text = editText.getText().toString();
                    return new ClientSendMessage(uuid, now, text);
                });
    }

    /**
     * 接收的消息流
     */
    private Observable<Message> createReceiveMessageStream() {
        // 收到的消息流
        return Observable.create((ObservableEmitter<Message> emitter) -> {
            try {
                if (this.socketClient != null) {
                    while (!this.socketClient.isTerminate()) {
                        Message message = this.socketClient.readFromServer();
                        Log.d("收到消息", message.toString());
                        emitter.onNext(message);
                    }
                    emitter.onComplete();
                }
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io());    // 上面的接收是网络操作，要在io中做
    }
}
