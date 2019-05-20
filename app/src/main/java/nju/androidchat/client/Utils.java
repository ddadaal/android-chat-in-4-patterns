package nju.androidchat.client;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;
import java.util.Properties;

import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;
import nju.androidchat.client.mvvm0.Mvvm0TalkActivity;

import static android.content.Context.INPUT_METHOD_SERVICE;

@Log
@UtilityClass
public class Utils {
    Properties props = new Properties();
    String CHAT_ACTIVITY_KEY = "chat_activity";
    Class<?> CHAT_ACTIVITY = Mvvm0TalkActivity.class;

    public void jumpTo(AppCompatActivity activity, Class<?> clazz) {
        Intent intent = new Intent(activity.getBaseContext(), clazz);
        activity.startActivity(intent);
    }

    public void jumpToHome(AppCompatActivity activity) {
        jumpTo(activity, MainActivity.class);
    }

    public void jumpToChat(AppCompatActivity activity) {
        jumpTo(activity, CHAT_ACTIVITY);
    }


    public boolean hideKeyboard(AppCompatActivity activity) {
        InputMethodManager mInputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        return mInputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
    }

    public boolean send(int actionId, KeyEvent event) {
        return actionId == EditorInfo.IME_ACTION_SEND
                || actionId == EditorInfo.IME_ACTION_DONE
                || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction());
    }

    public void scrollListToBottom(AppCompatActivity activity) {
        ScrollView scrollView = activity.findViewById(R.id.content_scroll_view);
        scrollView.post(() -> {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        });
    }

    // 简单地判断一个内容里有没有脏话
    public boolean containsBadWords(String content) {
        return content.contains("fuck");
    }

}
