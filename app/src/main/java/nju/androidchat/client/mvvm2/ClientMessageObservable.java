package nju.androidchat.client.mvvm2;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nju.androidchat.client.BR;
import nju.androidchat.shared.message.ClientSendMessage;
import nju.androidchat.shared.message.ServerSendMessage;

@AllArgsConstructor
public class ClientMessageObservable extends BaseObservable {
    @Getter
    private UUID messageId;

    @Getter
    private LocalDateTime time;

    @Getter
    private String senderUsername;

    @Getter
    @Bindable
    private String message;

    @Getter
    private Direction direction;

    @Getter
    @Bindable
    private State state;

    public void setMessage(String message){
        this.message = message;
        notifyPropertyChanged(BR.message);
    }

    public void setState(State state){
        this.state = state;
        notifyPropertyChanged(BR.state);
    }

    public boolean isAlignLeft(){
        if(direction.equals(Direction.RECEIVE)){
            return false;
        }
        if(direction.equals(Direction.SEND)){
            return true;
        }
        return true;
    }

    public void setAlignLeft(){

    }

    public boolean isAlignRight(){
        if(direction.equals(Direction.RECEIVE)){
            return true;
        }
        if(direction.equals(Direction.SEND)){
            return false;
        }
        return false;
    }

    public void setAlignRight(){

    }

    public String getPicture(){
        if(direction.equals(Direction.RECEIVE)){
            return "@mipmap/ic_head_default_left";
        }
        if(direction.equals(Direction.SEND)){
            return "@mipmap/ic_head_default_right";
        }
        return "";
    }

    public void setPicture(){

    }

    public ClientMessageObservable(ClientSendMessage clientSendMessage, String username){
        direction = Direction.SEND;
        messageId = clientSendMessage.getId();
        time = clientSendMessage.getTime();
        message = clientSendMessage.getMessage();
        senderUsername = username;
    }

    public ClientMessageObservable(ServerSendMessage serverSendMessage){
        direction = Direction.RECEIVE;
        messageId = serverSendMessage.getMessageId();
        time = serverSendMessage.getTime();
        message = serverSendMessage.getMessage();
        senderUsername = serverSendMessage.getSenderUsername();
        state = State.SENT;
    }
}
