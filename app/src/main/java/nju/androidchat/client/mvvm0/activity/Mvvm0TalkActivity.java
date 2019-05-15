package nju.androidchat.client.mvvm0.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.extern.java.Log;
import nju.androidchat.client.BR;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.mvvm0.viewmodel.Mvvm0ListAdapter;
import nju.androidchat.client.mvvm0.model.ClientMessageObservable;
import nju.androidchat.client.mvvm0.viewmodel.Mvvm0ViewModel;
import nju.androidchat.shared.message.ClientSendMessage;

@Log
public class Mvvm0TalkActivity extends AppCompatActivity {
    private Mvvm0ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new Mvvm0ViewModel();
        setContentView(R.layout.activity_main_mvvm);
        ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main_mvvm);
        Mvvm0ListAdapter adapter = new Mvvm0ListAdapter(getLayoutInflater(), viewModel.getMessageObservableList());
        binding.setVariable(BR.viewModel, viewModel);
        binding.setVariable(BR.adapter, adapter);

//        // 测试用
//        initData();
    }


    private void initData() {
        for (int i = 0; i < 10; i++) {
            String message = "This is the " + i + " message!";
            LocalDateTime now = LocalDateTime.now();
            UUID uuid = UUID.randomUUID();
            String senderUsername = "Somebody";
            ClientSendMessage clientSendMessage = new ClientSendMessage(uuid, now, message);
            viewModel.getMessageObservableList().add(new ClientMessageObservable(clientSendMessage, senderUsername));
        }
    }

    @Override
    public void onBackPressed() {
        AsyncTask.execute(() -> {
            viewModel.disconnect();
        });

        Utils.jumpToHome(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            log.info("not on focus");
            return hideKeyboard();
        }
        return super.onTouchEvent(event);
    }

    private boolean hideKeyboard() {
        return Utils.hideKeyboard(this);
    }


    private void sendText() {
        viewModel.sendMessage();
    }

    public void onBtnSendClicked(View v) {
        hideKeyboard();
        sendText();
    }
}
