package ui;

import websocket.messages.ServerMessage;

public interface ServerObserver {
    public void notifyMessage(String json);

}
