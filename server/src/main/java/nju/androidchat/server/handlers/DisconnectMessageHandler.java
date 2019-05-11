package nju.androidchat.server.handlers;

import lombok.extern.java.Log;
import nju.androidchat.server.ChatServer;
import nju.androidchat.server.ConnectionHandler;
import nju.androidchat.shared.message.DisconnectMessage;

@Log
public class DisconnectMessageHandler implements MessageHandler<DisconnectMessage> {
    @Override
    public void handle(DisconnectMessage message, ConnectionHandler connectionHandler) {
        connectionHandler.log("Disconnect request received. Terminating connection of " + connectionHandler.getUsername());
        connectionHandler.setTerminate(true);
        ChatServer.connectionMap.remove(connectionHandler.getUsername());
    }
}
