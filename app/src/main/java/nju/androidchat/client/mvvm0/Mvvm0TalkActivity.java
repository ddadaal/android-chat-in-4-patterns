package nju.androidchat.client.mvvm0;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import lombok.extern.java.Log;
import nju.androidchat.client.BR;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.mvvm0.viewmodel.Mvvm0ListAdapter;
import nju.androidchat.client.mvvm0.viewmodel.Mvvm0ViewModel;

@Log
public class Mvvm0TalkActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
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
}
