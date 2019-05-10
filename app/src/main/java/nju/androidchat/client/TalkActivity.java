package nju.androidchat.client;

import android.os.Bundle;

import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


public class TalkActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView editText = findViewById(R.id.et_content);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            //当actionId == XX_SEND 或者 XX_DONE时都触发
            //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
            //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
            if (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                hideKeyboard();
                //处理事件
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
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
        return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }
}
