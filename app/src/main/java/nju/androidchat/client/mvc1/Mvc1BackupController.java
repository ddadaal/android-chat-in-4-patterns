package nju.androidchat.client.mvc1;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Mvc1BackupController {

    public Mvc1BackupModel model;

    public void backup() {
        model.backup();
    }

}
