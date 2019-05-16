package nju.androidchat.server.handlers;

import lombok.extern.java.Log;
import nju.androidchat.server.ConnectionHandler;
import nju.androidchat.shared.message.ClientSendMessage;
import nju.androidchat.shared.message.ServerSendMessage;

@Log
public class ClientSendMessageHandler implements MessageHandler<ClientSendMessage> {
    @Override
    public void handle(ClientSendMessage message, ConnectionHandler connectionHandler) {
        String messageContent = message.getMessage();

        log.info("有客户端发来消息" + messageContent);
        // received a message, send it to all clients except sender
        connectionHandler.sendToAllOtherClients(new ServerSendMessage(
                message.getMessageId(),
                message.getTime(),
                connectionHandler.getUsername(),
                messageContent
        ));
    }
}
