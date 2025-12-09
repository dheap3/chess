package ui;

import websocket.messages.ServerMessage;

public interface ServerObserver {
    public void notify(ServerMessage msg);

}
