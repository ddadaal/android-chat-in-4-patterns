package nju.androidchat.client.mvp1;

import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
public class Mvp1BackupPresenter implements Mvp1Contract.BackupPresenter {

    private Mvp1Contract.BackupModel backupModel;
    private Mvp1Contract.BackupView backupView;

    @Override
    public void backup() {
        this.backupView.editBtnStatusAndText(false, "正在备份");
        this.backupModel.backup();
        this.backupView.editBtnStatusAndText(true, "备份");
        this.backupView.editTextView(this.backupModel.getLastUpdated().toString());
    }

    @Override
    public void start() {

    }
}
