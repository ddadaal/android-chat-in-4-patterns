package nju.androidchat.client;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import lombok.extern.java.Log;

import java.util.Objects;

@Log
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Button btn1 = findViewById(R.id.btn_link);
        EditText editText1 = findViewById(R.id.ip_input);
        EditText editText2 = findViewById(R.id.username_input);


        btn1.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            if (linkToServer(editText1.getText().toString(), editText2.getText().toString())) {
                Intent intent = new Intent(MainActivity.this, TalkActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "请输入信息！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean linkToServer(String ip, String username) {
        log.info("ip: " + ip);
        log.info("username: " + username);

        return !(ip.equals("") || username.equals(""));
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
