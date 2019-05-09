package nju.androidchat.server;

import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.UUID;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import nju.androidchat.shared.message.ClientSendMessage;
import nju.androidchat.shared.message.DisconnectMessage;
import nju.androidchat.shared.message.Message;
import nju.androidchat.shared.message.MessageHandler;
import nju.androidchat.shared.message.MessageUtils;
import nju.androidchat.shared.message.RecallMessage;
import nju.androidchat.shared.message.RecallRequestMessage;
import nju.androidchat.shared.message.ServerSendMessage;

@Log
public class ConnectionHandler implements Runnable, Closeable {

    private String username;

    private Socket socket;

    private boolean terminate = false;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ConnectionHandler(String username, Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.username = username;
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    private static Hashtable<Class<?>, Method> handlerTable = new Hashtable<>();

    static {
        MessageUtils.fillHandlerMap(ConnectionHandler.class, handlerTable);
    }

    private void log(String message) {
        log.info(String.format("[%s]: %s", username, message));
    }


    @MessageHandler
    public void handleDisconnect(DisconnectMessage message) {
        log("Disconnect request received. Terminating connection.");
        terminate = true;
        ChatServer.connectionMap.remove(this.username);
    }

    @MessageHandler
    public void handleClientSendMessage(ClientSendMessage message) {
        String messageContent = message.getMessage();
        log("Received message: " + messageContent);

        // received a message, send it to all clients except sender
        sendToAllClients(new ServerSendMessage(
                UUID.randomUUID(),
                message.getTime(),
                this.username,
                messageContent
        ));
    }

    private void sendToAllClients(Message message) {
        for (ConnectionHandler connection : ChatServer.connectionMap.values()) {
            if (connection != this) {
                connection.writeToClient(message);
            }
        }
    }

    @MessageHandler
    public void handleRecallMessage(RecallRequestMessage message) {
        UUID messageId = message.getMessageId();
        log("Recall request received: " + messageId);

        // no sender validation
        sendToAllClients(new RecallMessage(messageId));

    }

    @Override
    public void run() {

        // init streams
        try {
            while (!terminate) {
                Message message = readFromClient();

                if (message == null) {
                    continue;
                }

                // choose Method to call
                Method method = handlerTable.get(message.getClass());
                if (method != null) {
                    method.invoke(this, message);
                } else {
                    log.warning("Unknown message type: " + message.getClass().getSimpleName());
                }

            }
        } catch (Exception e) {
            log.severe("Exception occurred: " + e.toString());
        } finally {
            try {
                this.close();
            } catch (IOException e) {
                log.severe("Exception occurred during closing: " + e.toString());
            }
        }
    }

    @SneakyThrows
    private void writeToClient(Message message) {
        out.writeObject(message);
    }

    private @Nullable
    Message readFromClient() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }


    @Override
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
