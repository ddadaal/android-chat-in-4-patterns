package nju.androidchat.client.mvvm5.viewmodel;

public interface UiOperator {
    void runOnUiThread(Runnable action);
//    因为要识别是否在屏幕内，如果自动滚动到底部就没有识别的必要了，因为会全部阅读
//    void scrollListToBottom();
}
