package nju.androidchat.client.mvp1;

import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
public class Mvp1BackupPresenter implements Mvp1Contract.BackupPresenter {

    private Mvp1Contract.BackupModel backupModel;
    private Mvp1Contract.BackupView backupView;

    @Override
    public void backup() {
        // Presenter首先修改界面显示
        this.backupView.editBtnStatusAndText(false, "正在备份");

        // 再进行数据操作
        this.backupModel.backup();

        // 数据操作结束后，将界面改回来
        this.backupView.editBtnStatusAndText(true, "备份");
        this.backupView.editTextView(this.backupModel.getLastUpdated().toString());
    }

    @Override
    public void start() {

    }
}
