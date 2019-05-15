package nju.androidchat.client.mvc1;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

public class Mvc1BackupModel {

    public interface UpdatedListener {
        void onUpdated();
    }

    @Getter
    private LocalDateTime lastUpdated = null;

    @Getter @Setter
    private UpdatedListener listener;

    @SneakyThrows
    public void backup() {
        // Simulate a HTTP request
        Thread.sleep(3000);
        lastUpdated = LocalDateTime.now();
        if (listener != null) {
            listener.onUpdated();
        }
    }
}
