package ui;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;

public class WebSocketFacade implements WebSocket.Listener {

    private final Gson gson = new Gson();
    private WebSocket socket;

    public WebSocketFacade(String url) {
        url = (url.replace("http", "ws"));

        this.socket = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create(url + "/ws"), this)
                .join();
    }

    public void send(UserGameCommand cmd) {
        String json = gson.toJson(cmd);
//        System.out.println("Sending WS message: " + json);
        socket.sendText(json, true);
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        System.out.println("Connected to server WS");
        webSocket.request(1);
    }

    @Override
    public java.util.concurrent.CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
        System.out.println("Received WS message: " + data);
        ws.request(1);
        return null;
    }
}
