package server;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import websocket.commands.UserGameCommand;

import java.util.function.Consumer;

public class WebSocketHandler implements Consumer<WsConfig> {

    private final Gson gson = new Gson();

    @Override
    public void accept(WsConfig ws) {

        ws.onConnect(ctx -> {
            System.out.println("connect");
        });

        ws.onMessage(ctx -> {
            UserGameCommand cmd = gson.fromJson(ctx.message(), UserGameCommand.class);
            handle(ctx, cmd);
        });

        ws.onClose(ctx -> {
            System.out.println("close");
        });

        ws.onError(ctx -> {
            System.out.println("error");
        });
    }

    private void handle(WsMessageContext ctx, UserGameCommand cmd) {

        switch (cmd.getCommandType()) {
            case CONNECT -> System.out.println("CONNECT command received");
            case MAKE_MOVE -> System.out.println("MAKE_MOVE received");
            case LEAVE -> System.out.println("LEAVE received");
            case RESIGN -> System.out.println("RESIGN received");
        }

        // respond minimally so handshake doesn’t break
        ctx.send("ok");
    }
}
