package nju.androidchat.client.mvc1;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

public class Mvc1BackupModel {

    public interface BackupCompleteListener {
        void onBackupComplete();
    }

    public interface BackupStartListener {
        void onBackupStart();
    }

    @Getter
    private boolean backingUp = false;

    @Getter
    private LocalDateTime lastUpdated = null;

    @Getter @Setter
    private BackupCompleteListener backupCompleteListener;

    @Getter @Setter
    private BackupStartListener backupStartListener;

    @SneakyThrows
    public void backup() {

        // 修改正在备份状态数据
        backingUp = true;
        if (backupStartListener != null) {
            backupStartListener.onBackupStart();
        }

        // 发送HTTP请求（使用Thread.sleep替代）
        Thread.sleep(3000);

        // 备份完成，修改上次更新时间，修改正在备份状态
        lastUpdated = LocalDateTime.now();
        backingUp = true;

        if (backupCompleteListener != null) {
            backupCompleteListener.onBackupComplete();
        }



    }
}
