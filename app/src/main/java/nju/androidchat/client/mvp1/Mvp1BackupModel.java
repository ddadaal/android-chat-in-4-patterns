package nju.androidchat.client.mvp1;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
public class Mvp1BackupModel implements Mvp1Contract.BackupModel {

    @Setter
    private Mvp1Contract.BackupPresenter iMvp1BackupPresenter;

    @Getter
    private LocalDateTime lastUpdated = null;

    @Override
    public void backup() {
        // Simulate a HTTP request
        try {
            Thread.sleep(3000);
            lastUpdated = LocalDateTime.now();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
