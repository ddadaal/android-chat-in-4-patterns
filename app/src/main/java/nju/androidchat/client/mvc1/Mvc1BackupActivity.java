package nju.androidchat.client.mvc1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import nju.androidchat.client.R;

public class Mvc1BackupActivity extends AppCompatActivity implements Mvc1BackupModel.UpdatedListener {

    private Mvc1BackupController controller;
    private Mvc1BackupModel model;
    private TextView txLastUpdated;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup);

        txLastUpdated = findViewById(R.id.last_updated_lb);

        model = new Mvc1BackupModel();
        model.setListener(this);
        controller = new Mvc1BackupController(model);

        onUpdated();

    }

    public void onBtnBackupClicked(View view) {
        AsyncTask.execute(() -> {
            controller.backup();
        });

    }

    @Override
    public void onUpdated() {
        runOnUiThread(() -> {
            if (model.getLastUpdated() == null){
                txLastUpdated.setText("从未更新");
            } else {
                Toast.makeText(this, "备份成功！", Toast.LENGTH_SHORT).show();
                txLastUpdated.setText(model.getLastUpdated().toString());
            }
        });

    }

}
