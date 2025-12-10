package server;

import chess.ChessGame;
import com.google.gson.Gson;
import datamodel.GameData;
import datamodel.ListGamesResponse;
import io.javalin.websocket.*;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WebSocketHandler implements Consumer<WsConfig> {

    private final Gson gson = new Gson();
    private GameService gameService = null;   // add this
    //create of list of contexts to save (new context for each client)
    Map<Integer, ArrayList<WsContext>> gameSessions = new HashMap<>();

    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void accept(WsConfig ws) {

        ws.onConnect(ctx -> {
            ctx.enableAutomaticPings();
//            System.out.println("connect");
        });

        ws.onMessage(ctx -> {
            UserGameCommand cmd = gson.fromJson(ctx.message(), UserGameCommand.class);
            handle(ctx, cmd);
        });

        ws.onClose(ctx -> {
            gameSessions.values().forEach(list -> list.remove(ctx));
//            System.out.println("close");
        });

        ws.onError(ctx -> {
            System.out.println("error");
        });
    }

    private void handle(WsMessageContext ctx, UserGameCommand cmd) {
        //this switch statement uses the nicer syntax, update other switches to match this one
        switch (cmd.getCommandType()) {
            case CONNECT -> {
                //add the session to the gameSessions
                int gameID = cmd.getGameID();
                gameSessions.putIfAbsent(gameID, new ArrayList<>());
                gameSessions.get(gameID).add(ctx);
                //Server sends a LOAD_GAME message back to the root client.
                //Server sends a Notification message to all other clients in that game informing them the root
                // client connected to the game, either as a player (in which case their color must be specified)
                // or as an observer.
                ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
                ChessGame game = gameService.getGame(cmd.getGameID()).game();
                msg.setGame(game);
                String json = gson.toJson(msg);
                ctx.send(json);
                System.out.println("LOAD_GAME sent back");
                for (WsContext context : gameSessions.get(cmd.getGameID())) {
                    if (context != ctx) {//send it to other clients, not the root client
                        msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                        json = gson.toJson(msg);
                        context.send(json);
                        System.out.println("NOTIFICATION sent back");
                    }
                }
            }
            case MAKE_MOVE -> {
                //Server verifies the validity of the move. TODO
                //Game is updated to represent the move. Game is updated in the database.
                //Server sends a LOAD_GAME message to all clients in the game (including
                // the root client) with an updated game.
                ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
                String json = gson.toJson(msg);
                ctx.send(json);
                System.out.println("LOAD_GAME sent back");
                //Server sends a Notification message to all other clients in that game informing them what move was made.
                msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                json = gson.toJson(msg);
                ctx.send(json);
                System.out.println("NOTIFICATION sent back");
                //If the move results in check, checkmate or stalemate the server sends a
                // Notification message to all clients.
                if (false) {
                    msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                    json = gson.toJson(msg);
                    ctx.send(json);
                    System.out.println("NOTIFICATION sent back");
                }

            }
            case LEAVE -> {
                //If a player is leaving, then the game is updated to remove the root client. Game is updated in the database.
                //Server sends a Notification message to all other clients in that game informing them that
                // the root client left. This applies to both players and observers.
                //update game TODO
                System.out.println("Game removed root client and updated");
                ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                String json = gson.toJson(msg);
                ctx.send(json);
                System.out.println("NOTIFICATION sent back");
            }
            case RESIGN -> {
                //Server marks the game as over (no more moves can be made). Game is updated in the database.
                //Server sends a Notification message to all clients in that game informing them that the
                // root client resigned. This applies to both players and observers.
                //update game TODO
                System.out.println("Game marked as over and updated");
                ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                String json = gson.toJson(msg);
                ctx.send(json);
                System.out.println("NOTIFICATION sent back");
            }
        }
//        breaks the handshake
//        ctx.send("finished handling "+ cmd.getCommandType());
    }
}
