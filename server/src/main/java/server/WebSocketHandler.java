package server;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WebSocketHandler implements Consumer<WsConfig> {

    private final Gson gson = new Gson();
    private GameService gameService = null;
    private UserService userService = null;
    //create of list of contexts to save (new context for each client)
    Map<Integer, ArrayList<WsContext>> gameSessions = new HashMap<>();

    public WebSocketHandler(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
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

    private void handle(WsMessageContext ctx, UserGameCommand cmd) throws Exception{
        switch (cmd.getCommandType()) {
            case CONNECT -> {
                int gameID = cmd.getGameID();
                String auth = cmd.getAuthToken();
                //verify if the info received is in the database
                if (gameService.getGame(gameID) == null) {
                    ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                    msg.setErrorMessage("Game not found");
                    ctx.send(gson.toJson(msg));
                    break;
                }
                if (userService.authDAO.getAuth(auth) == null) {
                    ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                    msg.setErrorMessage("You are not authorized to access this game");
                    ctx.send(gson.toJson(msg));
                    break;
                }
                gameSessions.putIfAbsent(gameID, new ArrayList<>());
                if (!gameSessions.get(gameID).contains(ctx)) {
                    gameSessions.get(gameID).add(ctx);//add the session to the gameSessions
                }

                ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
                ChessGame game = gameService.getGame(cmd.getGameID()).game();
                msg.setGame(game);
                ctx.send(gson.toJson(msg));
//                System.out.println("LOAD_GAME sent back");
                for (WsContext context : gameSessions.get(cmd.getGameID())) {
                    if (!context.sessionId().equals(ctx.sessionId())) {//send it to other clients, not the root client
                        msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                        String user = cmd.getUser();
                        String type = cmd.getUserType();
                        msg.setMessage("User " + user + " connected (" + type + ")");
                        context.send(gson.toJson(msg));
//                        System.out.println("NOTIFICATION sent back");
                    }
                }
            }
            case MAKE_MOVE -> {
                //Server verifies the validity of the move.
                ChessMove move = cmd.getMove();
                ChessGame game = gameService.getGame(cmd.getGameID()).game();
                //checks if the game can be updated (if the state of the game isOver)
                if (game.isOver()) {
                    String message = "Game already over";
                    sendError(message, ctx);
                    break;
                }
                //Server verifies the validity of the move.
                if (!game.validMoves(move.getStartPosition()).contains(move)) {
                    System.out.println("Move not valid");
                    break;
                }
                //Game is updated to represent the move. Game is updated in the database.
                game.makeMove(move);
                GameData oldGameData = gameService.getGame(cmd.getGameID());
                GameData newGameData = new GameData(oldGameData.gameID(), oldGameData.whiteUsername(), oldGameData.blackUsername(), oldGameData.gameName(), game);
                gameService.gameDAO.updateGame(newGameData);
                //Server sends a LOAD_GAME message to all clients in the game (including
                // the root client) with an updated game.
                ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
                msg.setGame(game);
                for (WsContext context : gameSessions.get(cmd.getGameID())) {//to everyone
                    context.send(gson.toJson(msg));
                    System.out.println("LOAD_GAME sent back");
                }
                //Server sends a Notification message to all other clients in that game informing them what move was made.
                for (WsContext context : gameSessions.get(cmd.getGameID())) {
                    if (!context.sessionId().equals(ctx.sessionId())) {//send it to other clients, not the root client
                        msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                        String user = cmd.getUser();
                        String moveString;
                        if (move.getPromotionPiece() != null) {
                            moveString = move.getStartPosition() + " to " + move.getEndPosition();
                        } else {
                            moveString = move.toString();
                        }
                        msg.setMessage("User " + user + " made move " + moveString);
                        context.send(gson.toJson(msg));
                        System.out.println("NOTIFICATION sent back");
                    }
                }
                //If the move results in check, checkmate or stalemate the server sends a
                // Notification message to all clients.


            }
            case LEAVE -> {
                //If a player is leaving, then the game is updated to remove the root client. Game is updated in the database.
                //Server sends a Notification message to all other clients in that game informing them that
                // the root client left. This applies to both players and observers.
                //update game TODO
                System.out.println("Game removed root client and updated");
                ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                ctx.send(gson.toJson(msg));
                System.out.println("NOTIFICATION sent back");
            }
            case RESIGN -> {
                //Server marks the game as over (no more moves can be made). Game is updated in the database.
                //Server sends a Notification message to all clients in that game informing them that the
                // root client resigned. This applies to both players and observers.
                //update game TODO
                System.out.println("Game marked as over and updated");
                ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                ctx.send(gson.toJson(msg));
                System.out.println("NOTIFICATION sent back");
            }
        }
//        breaks the handshake
//        ctx.send("finished handling "+ cmd.getCommandType());
    }

    private ChessGame.TeamColor getUserColor(GameData gameData, String user) {
        if (gameData.whiteUsername() == user) {
            return ChessGame.TeamColor.WHITE;
        } else if (gameData.blackUsername() == user) {
            return ChessGame.TeamColor.BLACK;
        } else {
            System.out.println("User " + user + " not in game");
        }
        return null;
    }

    void notifyEveryone(UserGameCommand cmd, String message) {
        for (WsContext context : gameSessions.get(cmd.getGameID())) {//to everyone
            ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            msg.setMessage(message);
            context.send(gson.toJson(msg));
            System.out.println("NOTIFICATION sent back");
        }
    }

    void notifyEveryoneElse(UserGameCommand cmd, String message, WsContext currentContext) {
        for (WsContext context : gameSessions.get(cmd.getGameID())) {
            if (!context.sessionId().equals(currentContext.sessionId())) {//send it to other clients, not the root client
                ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                msg.setMessage(message);
                context.send(gson.toJson(msg));
//                        System.out.println("NOTIFICATION sent back");
            }
        }
    }
}
