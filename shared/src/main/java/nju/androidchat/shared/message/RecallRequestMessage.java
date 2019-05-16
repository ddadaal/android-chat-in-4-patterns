package nju.androidchat.shared.message;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class RecallRequestMessage extends Message {
    @Getter
    private UUID messageId;
}
