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

        // 初始流
        this.receiveMessage$ = this.createReceiveMessageStream().share();
        this.sendMessages$ = this.createSendMessageStream().share();
        // 分类过滤
        this.serverSendMessages$ = this.receiveMessage$
                .filter(message -> message instanceof ServerSendMessage)
                .map(message -> (ServerSendMessage) message);
        this.errorMessage$ = this.receiveMessage$
                .filter(message -> message instanceof ErrorMessage)
                .map(message -> (ErrorMessage) message);
        this.recallMessage$ = this.receiveMessage$
                .filter(message -> message instanceof RecallMessage)
                .map(message -> (RecallMessage) message);

        // 更新列表的流
        this.addToViewMessages$ = this.createMessageListItemStream(this.serverSendMessages$.share(), this.sendMessages$.share());



        // 订阅流
        this.sendMessages$
                .observeOn(Schedulers.io()) // 发送消息网络要在 io线程做
                .subscribe(this::onSendMessage, Throwable::printStackTrace);

        this.errorMessage$
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onErrorMessage, Throwable::printStackTrace);

        this.addToViewMessages$
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onAddViewToMessageList, Throwable::printStackTrace);

    }


    /* ============== 订阅处理函数 ============ */
    private void onAddViewToMessageList(View view) {
        log.info(view.toString());
        messageList.addView(view);
    }
    private void onErrorMessage(ErrorMessage message) {
        Toast.makeText(this, message.getErrorMessage(), Toast.LENGTH_LONG).show();
    }
    private void onSendMessage(ClientSendMessage message) {
        Log.d("send", message.toString());
        this.socketClient.writeToServer(message);
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

    /**
     * 更新列表的流
     * @param receive$
     * @param send$
     * @return
     */
    private Observable<LinearLayout> createMessageListItemStream(Observable<ServerSendMessage> receive$, Observable<ClientSendMessage> send$) {
        return Observable.merge(
                receive$.map(message -> new ItemTextReceive(this, message.getMessage())),
                send$.map(message -> new ItemTextSend(this, message.getMessage()))
        );
    }
}
