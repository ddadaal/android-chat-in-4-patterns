package nju.androidchat.client.mvvm3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import lombok.extern.java.Log;
import nju.androidchat.client.BR;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.mvvm3.viewmodel.Mvvm3ViewModel;
import nju.androidchat.client.mvvm3.viewmodel.UiOperator;

@Log
public class Mvvm3TalkActivity extends AppCompatActivity implements TextView.OnEditorActionListener, UiOperator {
    private Mvvm3ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mvvm3);

        viewModel = new Mvvm3ViewModel(this);

        ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main_mvvm3);
        binding.setVariable(BR.viewModel, viewModel);
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

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (Utils.send(actionId, event)) {
            hideKeyboard();
            // 异步地让Controller处理事件
            sendText();
        }
        return false;
    }

    @Override
    public void scrollListToBottom() {
        Utils.scrollListToBottom(this);
    }

    @Override
    public void sendBadWordNotice() {
        Toast.makeText(this, getString(R.string.bad_word_notice), Toast.LENGTH_SHORT).show();
    }
}
