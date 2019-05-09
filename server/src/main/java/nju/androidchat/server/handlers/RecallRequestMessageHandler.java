package nju.androidchat.server.handlers;

import java.util.UUID;

import nju.androidchat.server.ConnectionHandler;
import nju.androidchat.shared.message.RecallMessage;
import nju.androidchat.shared.message.RecallRequestMessage;

public class RecallRequestMessageHandler implements MessageHandler<RecallRequestMessage> {
    @Override
    public void handle(RecallRequestMessage message, ConnectionHandler connectionHandler) {
        UUID messageId = message.getMessageId();
        connectionHandler.log("Recall request received: " + messageId);

        // no sender validation
        connectionHandler.sendToAllOtherClients(new RecallMessage(messageId));
    }
}
