package nju.androidchat.shared.message;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class RecallMessage extends Message {
    @Getter
    private UUID messageId;
}
