package shared;

import java.io.Serializable;


public class NetworkMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    // Message types
    public static final int LOGIN = 1;
    public static final int LOGOUT = 2;
    public static final int JOIN_GAME = 3;
    public static final int MAKE_MOVE = 4;
    public static final int GAME_STATE = 5;
    public static final int CHAT_MESSAGE = 6;
    public static final int GAME_RESULT = 7;
    public static final int PLAYER_DISCONNECT = 8;
    public static final int PLAY_AGAIN = 9;
    public static final int RESIGN = 10;

    // Response status codes
    public static final String SUCCESS = "success";
    public static final String INVALID_CREDENTIALS = "invalid_credentials";
    public static final String USERNAME_EXISTS = "username_exists";
    public static final String WAITING_FOR_OPPONENT = "waiting_for_opponent";
    public static final String NOT_LOGGED_IN = "not_logged_in";
    public static final String INVALID_MOVE = "invalid_move";
    public static final int RESIGN_GAME = 8;
    private int messageType;
    private String sender;
    private Object content;


    public NetworkMessage(int type, String sender, Object content) {
        this.messageType = type;
        this.sender = sender;
        this.content = content;
    }


    public int getMessageType() {
        return messageType;
    }

    public String getSender() {
        return sender;
    }


    public Object getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "NetworkMessage[type=" + messageType + ", sender=" + sender + "]";
    }
}