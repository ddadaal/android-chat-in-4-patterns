package nju.androidchat.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import nju.androidchat.server.handlers.ClientSendMessageHandler;
import nju.androidchat.server.handlers.DisconnectMessageHandler;
import nju.androidchat.server.handlers.MessageHandler;
import nju.androidchat.server.handlers.RecallRequestMessageHandler;
import nju.androidchat.shared.message.ClientSendMessage;
import nju.androidchat.shared.message.DisconnectMessage;
import nju.androidchat.shared.message.ErrorMessage;
import nju.androidchat.shared.message.LoginRequestMessage;
import nju.androidchat.shared.message.LoginResponseMessage;
import nju.androidchat.shared.message.Message;
import nju.androidchat.shared.message.RecallRequestMessage;

@SuppressWarnings("unchecked")
@Log
public class ConnectionHandlerImpl implements ConnectionHandler, Runnable, Closeable {

    private String username;

    private Socket socket;

    private boolean terminate = false;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Map<Class<? extends Message>, MessageHandler<?>> handlerMap = new HashMap<>();

    public ConnectionHandlerImpl(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());

        // register handlers
        registerHandler(ClientSendMessage.class, new ClientSendMessageHandler());
        registerHandler(DisconnectMessage.class, new DisconnectMessageHandler());
        registerHandler(RecallRequestMessage.class, new RecallRequestMessageHandler());

    }

    private <T extends Message> void registerHandler(Class<T> messageClass, MessageHandler<T> handler) {
        handlerMap.put(messageClass, handler);
    }

    @Override
    public void log(String message) {
        log.info(String.format("[%s]: %s", username, message));
    }

    @Override
    public void sendToAllOtherClients(Message message) {
        for (ConnectionHandlerImpl connection : ChatServer.connectionMap.values()) {
            if (connection != this) {
                try {
                    connection.writeToClient(message);
                } catch (IOException e) {
                    log(String.format("Send message to %s failed. Cause: %s", connection.username, e.toString()));
                }
            }
        }
    }

    @Override
    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @SneakyThrows
    private Message readMessage() {
        Message m = (Message) in.readObject();
        log("Message Received: " + m);
        return m;
    }

    @Override
    public void run() {


        try {

            // wait for LoginRequestMessage first
            Message message = readMessage();

            if (message instanceof LoginRequestMessage) {
                LoginRequestMessage loginRequestMessage = (LoginRequestMessage) message;

                String username = loginRequestMessage.getUsername();

                // check username conflict
                if (!ChatServer.connectionMap.containsKey(username)) {
                    // login success
                    log(String.format("Login of %s from %s successful", username, socket.getRemoteSocketAddress()));
                    out.writeObject(new LoginResponseMessage(username));
                    ChatServer.connectionMap.put(username, this);
                    this.username = username;

                } else {
                    // username conflict
                    writeToClient(new ErrorMessage("Username conflict: " + username));
                    this.close();
                    return;
                }
            } else {
                writeToClient(new ErrorMessage("Expected LoginRequestMessage. Received: " +
                        (message == null ? "null" : message.getClass().getSimpleName())));
                this.close();
                return;
            }

            // Logged in. wait for incoming messages and handle
            while (!terminate) {
                message = readMessage();

                if (message == null) {
                    continue;
                }

                // choose Handler to call
                MessageHandler method = handlerMap.get(message.getClass());
                if (method != null) {
                    method.handle(message, this);
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

    private void writeToClient(Message message) throws IOException {
        out.writeObject(message);
    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
