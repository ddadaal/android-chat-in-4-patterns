package nju.androidchat.server.handlers;

import java.util.UUID;

import nju.androidchat.server.ConnectionHandler;
import nju.androidchat.shared.message.ClientSendMessage;
import nju.androidchat.shared.message.ServerSendMessage;

public class ClientSendMessageHandler implements MessageHandler<ClientSendMessage> {
    @Override
    public void handle(ClientSendMessage message, ConnectionHandler connectionHandler) {
        String messageContent = message.getMessage();
        UUID id = UUID.randomUUID();
        connectionHandler.log("Received message: " + messageContent);

        // received a message, send it to all clients except sender
        connectionHandler.sendToAllOtherClients(new ServerSendMessage(
                id,
                message.getTime(),
                connectionHandler.getUsername(),
                messageContent
        ));
    }
}
