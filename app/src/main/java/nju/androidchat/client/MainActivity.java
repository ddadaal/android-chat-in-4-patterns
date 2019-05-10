package nju.androidchat.client;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
            if(link_to_server(editText1.getText().toString(),editText2.getText().toString())) {
                Intent intent = new Intent(MainActivity.this, TalkActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean link_to_server(String ip, String username) {
        log.info("ip: "+ip);
        log.info("username: "+username);
        boolean successful = false;
        if(!(ip.equals("")||username.equals(""))){
            successful = true;
        }
        return successful;
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

    @Override
    public void onBackPressed() {
    }
}
