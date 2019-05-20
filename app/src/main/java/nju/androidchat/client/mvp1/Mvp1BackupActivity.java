package nju.androidchat.client.mvp1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import lombok.extern.java.Log;
import nju.androidchat.client.R;

@Log
public class Mvp1BackupActivity extends AppCompatActivity implements Mvp1Contract.BackupView {

    private Mvp1Contract.BackupPresenter presenter;
    private TextView txLastUpdated;
    private Button backupBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup);

        txLastUpdated = findViewById(R.id.last_updated_lb);
        backupBtn = findViewById(R.id.backup_btn);

        Mvp1BackupModel mvp1BackupModel = new Mvp1BackupModel();

        // Create the presenter
        this.presenter = new Mvp1BackupPresenter(mvp1BackupModel, this);
        mvp1BackupModel.setIMvp1BackupPresenter(this.presenter);
    }

    public void onBtnBackupClicked(View view) {
        AsyncTask.execute(() -> {
            presenter.backup();
        });
    }

    @Override
    public void editBtnStatusAndText(boolean canEdit, String text) {
        runOnUiThread(() -> {
            backupBtn.setText(text);
            backupBtn.setEnabled(canEdit);
        });
    }

    @Override
    public void editTextView(String text) {
        runOnUiThread(() -> txLastUpdated.setText(text));
    }

    @Override
    public void setPresenter(Mvp1Contract.BackupPresenter presenter) {
        this.presenter = presenter;
    }
}
