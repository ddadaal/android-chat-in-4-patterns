package nju.androidchat.client;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;
import nju.androidchat.client.mvc0.Mvc0TalkActivity;
import nju.androidchat.client.mvp0.Mvp0TalkActivity;

import static android.content.Context.INPUT_METHOD_SERVICE;

@UtilityClass
public class Utils {

    public final Class<?> CHAT_ACTIVITY = Mvp0TalkActivity.class;

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

}
