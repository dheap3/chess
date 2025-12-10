package ui;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private final Gson gson = new Gson();
    public Session session;

    public WebSocketFacade(String url, ServerObserver obs) throws Exception {
        URI uri = new URI(url.replace("http", "ws") + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String json) {//coming form the server
//                ServerMessage msg = gson.fromJson(json, ServerMessage.class);
                obs.notifyMessage(json);
                //handle message here
//                System.out.println(s);
            }
        });
    }

    public void send(UserGameCommand cmd) throws Exception{
        String json = gson.toJson(cmd);
        session.getBasicRemote().sendText(json);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
