package nju.androidchat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.java.Log;
import nju.androidchat.shared.Shared;
import nju.androidchat.shared.message.ErrorMessage;
import nju.androidchat.shared.message.LoginRequestMessage;
import nju.androidchat.shared.message.LoginResponseMessage;
import nju.androidchat.shared.message.Message;
import nju.androidchat.shared.message.MessageUtils;

import static nju.androidchat.shared.Shared.SERVER_PORT;

@Log
public class ChatServer {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ChatServer server = new ChatServer();

        server.startServer();

    }

    // Record all connected clients
    public static Map<String, ConnectionHandler> connectionMap = new HashMap<>();

    public void startServer() throws IOException, ClassNotFoundException {

        ServerSocket server = new ServerSocket(SERVER_PORT);
        log.info("Server started. Awaiting incoming connection.");
        while (true) {
            Socket client = server.accept();

            log.info("Received message from " + client.getRemoteSocketAddress().toString());

            // get first message and see if it is login request

            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());

            Message message = (Message) in.readObject();
            if (message instanceof LoginRequestMessage) {
                LoginRequestMessage loginRequestMessage = (LoginRequestMessage) message;

                String username = loginRequestMessage.getUsername();

                // check username conflict
                if (!connectionMap.containsKey(username)) {
                    out.writeObject(new LoginResponseMessage(username));
                    ConnectionHandler handler = new ConnectionHandler(username, client, in, out);
                    connectionMap.put(username, handler);
                    new Thread(handler).start();

                } else {
                    // username conflict
                    errorAndClose(out,
                            "Username conflict: " + username);
                    client.close();
                }
            } else {
                errorAndClose(out,
                        "Expected LoginRequestMessage. Received: " +
                                (message == null ? "null" : message.getClass().getSimpleName()));
                client.close();
            }
        }
    }

    public void errorAndClose(ObjectOutputStream out, String message) throws IOException {
        out.writeObject(new ErrorMessage(message));
        out.close();
    }


}
