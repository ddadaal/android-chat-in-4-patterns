package nju.androidchat.client;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import lombok.extern.java.Log;
import nju.androidchat.client.frp0.Frp0TalkActivity;
import nju.androidchat.client.socket.SocketClient;
import nju.androidchat.shared.Shared;

import java.util.Objects;

@Log
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        EditText editText1 = findViewById(R.id.ip_input);

        editText1.setText(SocketClient.SERVER_ADDRESS + ":" + Shared.SERVER_PORT);

    }

    public void onBtnConnectClicked(View view) {
        Handler handler = new Handler();
        EditText editText1 = findViewById(R.id.ip_input);
        EditText editText2 = findViewById(R.id.username_input);

        String ip = editText1.getText().toString();
        String username = editText2.getText().toString();

        if (!(ip.equals("") || username.equals(""))) {


            AsyncTask.execute(() -> {
                String result = SocketClient.connect(username);
                if (result.equals("SUCCESS")) {
                    handler.post(() -> {
                        Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();

//                        Utils.jumpToChat(this);
                        Utils.jumpTo(this, Frp0TalkActivity.class);
                    });
                } else {
                    handler.post(() -> {
                        Toast.makeText(this, "连接失败！原因：" + result, Toast.LENGTH_SHORT).show();
                    });
                }
            });

        } else {
            Toast.makeText(this, "请输入信息！", Toast.LENGTH_SHORT).show();
        }
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

}
