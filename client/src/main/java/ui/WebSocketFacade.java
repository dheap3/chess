package ui;

import com.google.gson.Gson;
//import org.glassfish.tyrus.core.wsadl.model.Endpoint;
import ui.ServerObserver;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;

public class WebSocketFacade extends Endpoint {

    private final Gson gson = new Gson();
    public Session session;

    public WebSocketFacade(String url, ServerObserver obs) throws Exception {
        URI uri = new URI(url.replace("http", "ws") + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String s) {
                obs.notify();
                //handle message here
                System.out.println(s);
            }
        });
    }

    public void send(UserGameCommand cmd) throws Exception{
        String json = gson.toJson(cmd);
//        System.out.println("Sending WS message: " + json);
//        socket.sendText(json, true);
        session.getBasicRemote().sendText(json);
    }


//    @Override
//    public void onOpen(WebSocket webSocket) {
//        System.out.println("Connected to server WS");
//        webSocket.request(1);
//    }

//    @Override
//    public java.util.concurrent.CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
//        System.out.println("Received WS message: " + data);
//        String dataString = data.toString();
//        ServerMessage msg = gson.fromJson(dataString, ServerMessage.class);
//        switch (msg.getServerMessageType()) {
//            case LOAD_GAME -> {
//                //redraw the chessboard
//                //boardText.printBoard()
//            }
//            case ERROR -> {
//
//            }
//            case NOTIFICATION -> {
//
//            }
//        }
//        ws.request(1);
//        return null;
//    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
