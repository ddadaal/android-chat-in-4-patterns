package nju.androidchat.client.socket;

import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import lombok.SneakyThrows;
import nju.androidchat.shared.message.DisconnectMessage;
import nju.androidchat.shared.message.Message;

/**
 * 看一下服务器的ConnectionHandler的写法
 */
public class SocketClient implements Closeable {

    private String username;

    private Socket socket;

    private boolean terminate = false;

    // socket的输入和输出流
    // 在开始操作之前初始化这两个field
    /*
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
     */
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public SocketClient(String username, Socket socket) {
        this.username = username;
        this.socket = socket;

    }

    // 向服务器发送Message
    @SneakyThrows
    private void writeToServer(Message message) {
        out.writeObject(message);
    }

    // 从服务端读取信息，当没有读取到消息的时候阻塞
    // 直接开个线程循环调用这个方法
    // 看server.ConnectionHandler
    private @Nullable Message readFromServer() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }

    // 通知服务器关掉连接
    public void disconnect() {
        writeToServer(new DisconnectMessage());
    }

    // 结束的时候记得调用这个方法
    // 通知服务器关掉连接并关掉资源
    @Override
    public void close() throws IOException {
        disconnect();
        in.close();
        out.close();
        socket.close();
    }
}
