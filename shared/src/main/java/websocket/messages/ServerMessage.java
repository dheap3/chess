package websocket.messages;

import chess.ChessGame;

import javax.print.DocFlavor;
import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    ChessGame game;
    String message;
    String errorMessage;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }
    public ChessGame getGame() {
        return this.game;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
