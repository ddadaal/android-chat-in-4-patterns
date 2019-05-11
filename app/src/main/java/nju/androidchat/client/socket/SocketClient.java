package nju.androidchat.client.socket;


import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import nju.androidchat.shared.message.DisconnectMessage;
import nju.androidchat.shared.message.Message;

/**
 * 看一下服务器的ConnectionHandler的写法
 */
@Log
public class SocketClient implements Closeable, Runnable {


    private Thread thread;

    @Getter private String username;

    private Socket socket;

    private volatile boolean terminate = false;

    private MessageListener messageListener;

    // socket的输入和输出流
    // 在开始操作之前初始化这两个field
    /*
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
     */
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public static SocketClient client;

    private SocketClient(String username, Socket socket) throws IOException {
        this.username = username;
        this.socket = socket;

        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
    }

    public static void init(String username, Socket socket, MessageListener listener) throws IOException {
        if (client != null) {
            client.close();
        }
        client = new SocketClient(username, socket);
        client.messageListener = listener;
    }

    public void startListening() {
        client.thread = new Thread(client);
        client.thread.start();
    }


    /**
     * 向服务器发送Message
     */
    @SneakyThrows
    private void writeToServer(Message message) {
        out.writeObject(message);
    }

    // 从服务端读取信息，当没有读取到消息的时候阻塞
    // 直接开个线程循环调用这个方法
    // 看server.ConnectionHandler.run
    private Message readFromServer() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }

    /**
     *  通知服务器关掉连接
     *  不需要等待回复!
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

    @Override
    public void run() {
        try {
            while (!terminate) {
                Message message = readFromServer();
                messageListener.onMessageReceived(message);

            }
        } catch (Exception e) {
            log.severe("[Client] Exception occurred: " + e.toString());
        }

    }
}
