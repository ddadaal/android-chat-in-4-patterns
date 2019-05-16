package nju.androidchat.client.frp0;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import nju.androidchat.client.Utils;
import nju.androidchat.client.component.ItemTextReceive;
import nju.androidchat.client.component.ItemTextSend;
import nju.androidchat.client.component.OnRecallMessageRequested;
import nju.androidchat.client.socket.SocketClient;
import nju.androidchat.shared.message.ClientSendMessage;
import nju.androidchat.shared.message.ErrorMessage;
import nju.androidchat.shared.message.Message;
import nju.androidchat.shared.message.RecallMessage;
import nju.androidchat.shared.message.ServerSendMessage;

@lombok.extern.java.Log
public class Frp0TalkActivity extends AppCompatActivity implements OnRecallMessageRequested {

    private SocketClient socketClient;
    private Observable<ClientSendMessage> sendMessages$ = Observable.empty();
    private Observable<Message> receiveMessage$ = Observable.empty();
    private Observable<ServerSendMessage> serverSendMessages$ = Observable.empty();
    private Observable<ErrorMessage> errorMessage$ = Observable.empty();
    private Observable<RecallMessage> recallMessage$ = Observable.empty();
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

        // 1. 初始化发送流
        this.sendMessages$ = this.createSendMessageStream().share();

        // 2. 初始化接受信息流
        this.receiveMessage$ = this.createReceiveMessageStream().share();

        // 3. 将接受信息流分为多个流，分别处理
        // 3.1 错误处理流
        this.errorMessage$ = this.receiveMessage$
                .filter(message -> message instanceof ErrorMessage)
                .map(message -> (ErrorMessage) message);
        // 3.2 服务器发送消息流
        this.serverSendMessages$ = this.receiveMessage$
                .filter(message -> message instanceof ServerSendMessage)
                .map(message -> (ServerSendMessage) message);
        // 3.2 撤回消息流
        this.recallMessage$ = this.receiveMessage$
                .filter(message -> message instanceof RecallMessage)
                .map(message -> (RecallMessage) message);


        // 4. 处理每个流
        // 4.1 处理错误流
        this.errorMessage$
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((message) -> {
                    Toast.makeText(this, message.getErrorMessage(), Toast.LENGTH_LONG).show();
                }, Throwable::printStackTrace);


        // 4.2 处理发送流，将每个消息写到服务器
        this.sendMessages$
                .observeOn(Schedulers.io()) // 发送消息网络要在 io线程做
                .subscribe((message) -> {
                    Log.d("send", message.toString());
                    this.socketClient.writeToServer(message);
                }, Throwable::printStackTrace);


        // 4.3 合并发送流和服务器接受消息流，并更新UI
        this.addToViewMessages$ = Observable.merge(
                this.serverSendMessages$.share().map(message -> new ItemTextReceive(this, message.getMessage(), message.getMessageId())),
                this.sendMessages$.map(message -> new ItemTextSend(this, message.getMessage(), message.getMessageId(), this))
        );

        this.addToViewMessages$
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((view) -> {
                    log.info(view.toString());
                    messageList.addView(view);
                    Utils.scrollListToBottom(this);
                }, Throwable::printStackTrace);

    }


    /* ========= 创建流函数 ========= */

    /**
     * 发送出去的消息流
     */
    @SuppressLint("CheckResult")
    private Observable<ClientSendMessage> createSendMessageStream() {
        // 发送按钮事件
        Observable<Unit> clicks$ = RxView.clicks(sendButton).share();

        return clicks$
                .observeOn(AndroidSchedulers.mainThread())
                .map(e -> {
                    UUID uuid = UUID.randomUUID();
                    LocalDateTime now = LocalDateTime.now();
                    String text = editText.getText().toString();
                    editText.setText("");
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

    @Override
    public void onRecallMessageRequested(UUID messageId) {

    }
}
