package nju.androidchat.client.mvc1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import nju.androidchat.client.R;

public class Mvc1BackupActivity extends AppCompatActivity {

    private Mvc1BackupController controller;
    private Mvc1BackupModel model;
    private Button btnBackup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup);

        model = new Mvc1BackupModel();
        controller = new Mvc1BackupController(model);

        TextView txLastUpdated = findViewById(R.id.last_updated_lb);

        Button btnBackup = findViewById(R.id.backup_btn);


        // 订阅备份开始事件
        model.setBackupStartListener(() -> {
            runOnUiThread(() -> {
                btnBackup.setText("正在备份");
                btnBackup.setEnabled(false);
            });
        });

        // 订阅备份完成事件
        model.setBackupCompleteListener(() -> {
            runOnUiThread(() -> {
                if (model.getLastUpdated() == null){
                    txLastUpdated.setText("正在备份");
                } else {
                    Toast.makeText(this, "备份成功！", Toast.LENGTH_SHORT).show();
                    txLastUpdated.setText(model.getLastUpdated().toString());
                }
                btnBackup.setText("备份");
                btnBackup.setEnabled(true);
            });
        });


        txLastUpdated.setText("从未更新");

    }

    public void onBtnBackupClicked(View view) {
        AsyncTask.execute(() -> {
            controller.backup();
        });

    }
}
