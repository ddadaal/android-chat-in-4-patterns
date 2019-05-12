package nju.androidchat.shared.message;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class RecallRequestMessage extends Message {
    @Getter
    private UUID messageId;
}
