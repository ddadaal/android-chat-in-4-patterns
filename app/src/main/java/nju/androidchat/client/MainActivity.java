package nju.androidchat.client;

import android.annotation.SuppressLint;
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
import nju.androidchat.client.socket.SocketClient;
import nju.androidchat.shared.Shared;

import java.io.IOException;
import java.util.Objects;

@Log
public class MainActivity extends AppCompatActivity {

    EditText inputIp;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Utils.props.load(getResources().getAssets().open("config.properties"));
            String chatActivityClassName = Utils.props.getProperty(Utils.CHAT_ACTIVITY_KEY);
            if (chatActivityClassName != null) {
                Class classRead = Class.forName(chatActivityClassName);
                log.info("Current chatActivity is: " + chatActivityClassName);
                Utils.CHAT_ACTIVITY = classRead;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        inputIp = findViewById(R.id.ip_input);


        inputIp.setText(SocketClient.SERVER_ADDRESS + ":" + Shared.SERVER_PORT);
    }

    public void onBtnConnectClicked(View view) {
        Handler handler = new Handler();
        EditText editText2 = findViewById(R.id.username_input);

        String ip = inputIp.getText().toString();
        String username = editText2.getText().toString();

        if (!(ip.equals("") || username.equals(""))) {


            AsyncTask.execute(() -> {
                String result = SocketClient.connect(username, ip);
                if (result.equals("SUCCESS")) {
                    handler.post(() -> {
                        Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();

                        Utils.jumpToChat(this);
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
