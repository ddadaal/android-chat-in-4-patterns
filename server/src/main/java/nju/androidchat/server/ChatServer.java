package nju.androidchat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.java.Log;
import nju.androidchat.shared.message.ErrorMessage;
import nju.androidchat.shared.message.LoginRequestMessage;
import nju.androidchat.shared.message.LoginResponseMessage;
import nju.androidchat.shared.message.Message;

import static nju.androidchat.shared.Shared.SERVER_PORT;

@Log
public class ChatServer {
    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer();

        server.startServer();

    }

    // Record all connected clients
    public static Map<String, ConnectionHandlerImpl> connectionMap = new ConcurrentHashMap<>();

    public void startServer() throws IOException {

        ServerSocket server = new ServerSocket(SERVER_PORT);
        log.info(String.format("[Server] Server started on %s. Awaiting incoming connection.", SERVER_PORT));
        while (true) {
            Socket client = server.accept();

            log.info("Received connection from " + client.getRemoteSocketAddress().toString());

            // get first message and see if it is login request

            new Thread(new ConnectionHandlerImpl(client)).start();

        }
    }

}
