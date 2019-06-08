package nju.androidchat.client.socket;


import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import nju.androidchat.client.MainActivity;
import nju.androidchat.shared.Shared;
import nju.androidchat.shared.message.DisconnectMessage;
import nju.androidchat.shared.message.ErrorMessage;
import nju.androidchat.shared.message.LoginRequestMessage;
import nju.androidchat.shared.message.LoginResponseMessage;
import nju.androidchat.shared.message.Message;

/**
 * 看一下服务器的ConnectionHandler的写法
 */
@Log
public class SocketClient implements Closeable, Runnable {

    private Thread thread;

    @Getter
    private String username;

    private Socket socket;

    @Getter
    private volatile boolean terminate = false;

    private @Setter
    MessageListener messageListener;

    public final static String SERVER_ADDRESS = "10.0.2.2";

    // socket的输入和输出流
    // 在开始操作之前初始化这两个field
    /*
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
     */
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private @Getter
    static SocketClient client;


    private SocketClient(String username, Socket socket) throws IOException {
        this.username = username;
        this.socket = socket;

        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    @SneakyThrows
    public static String connect(String username, String target) {

        String[] splitted = target.split(":");

        try {
            if (client != null) {
                client.close();
            }
            Socket socket = new Socket(splitted[0], Integer.parseInt(splitted[1]));
            SocketClient client = new SocketClient(username, socket);
            log.info("Socket connection established.");

            // send login request
            log.info("Sending LoginRequestMessage");
            client.writeToServer(new LoginRequestMessage(username));
            Message message = client.readFromServer();
            if (message instanceof LoginResponseMessage) {
                LoginResponseMessage loginResponseMessage = (LoginResponseMessage) message;
                log.info("LoginResponseMessage received. Login successful");
                if (loginResponseMessage.getLoggedInUsername().equals(username)) {
                    SocketClient.client = client;
                    return "SUCCESS";
                }
            } else if (message instanceof ErrorMessage) {
                return ((ErrorMessage) message).getErrorMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.severe(e.toString());
            if (client != null) {
                client.close();
            }
            return e.toString();
        }
        return "";
    }

    @SneakyThrows
    public static String connect(String username) {

        return connect(username, SocketClient.SERVER_ADDRESS + ":" + Shared.SERVER_PORT);
    }


    @SneakyThrows
    public static void disconnectCurrent() {
        client.disconnect();
    }

    public void startListening() {
        client.thread = new Thread(client);
        client.thread.start();
    }

    @Override
    public void run() {
        try {
            while (!terminate) {
                Message message = readFromServer();
                if (messageListener != null) {
                    messageListener.onMessageReceived(message);
                }
            }
        } catch (Exception e) {
            log.severe("[Client] Exception occurred: " + e.toString());
        }

    }

    /**
     * 向服务器发送Message
     */
    @SneakyThrows
    public void writeToServer(Message message) {
        out.writeObject(message);
    }


    @SneakyThrows
    // 从服务端读取信息，当没有读取到消息的时候阻塞
    // 直接开个线程循环调用这个方法
    // 看server.ConnectionHandler.run
    public Message readFromServer() throws IOException {
        return (Message) in.readObject();
    }

    /**
     * 通知服务器关掉连接
     * 不需要等待回复!
     */
    public void disconnect() {
        writeToServer(new DisconnectMessage());
    }

    // 结束的时候记得调用这个方法
    // 通知服务器关掉连接并关掉资源
    @Override
    public void close() throws IOException {
        this.terminate = true;
        disconnect();
        in.close();
        out.close();
        socket.close();
    }


}
