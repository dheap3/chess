package server;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import datamodel.GameData;
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
                    sendError("Game not found", ctx);
                    break;
                }
                if (userService.authDAO.getAuth(auth) == null) {
                    sendError("You are not authorized to access this game", ctx);
                    break;
                }
                gameSessions.putIfAbsent(gameID, new ArrayList<>());
                if (!gameSessions.get(gameID).contains(ctx)) {
                    gameSessions.get(gameID).add(ctx);//add the session to the gameSessions
                }

                loadMyGame(cmd.getGameID(), ctx);

                String user = cmd.getUser();
                String type = cmd.getUserType();
                notifyEveryoneElse("User " + user + " connected (" + type + ")", cmd.getGameID(), ctx);
            }
            case MAKE_MOVE -> {
                ChessMove move = cmd.getMove();
                ChessGame game = gameService.getGame(cmd.getGameID()).game();
                //checks if the game can be updated (if the state of the game isOver)
                if (game.isOver()) {
                    String message = "Game already over";
                    sendError(message, ctx);
                    break;
                }
                //Checks if the user sending the command is authorized
                String rootAuth = cmd.getAuthToken();
                if (!userService.authDAO.dbContains(rootAuth)) {//rootClient's auth is in the db
                    sendError("You are not authorized", ctx);
                    break;
                }
                //Server verifies the validity of the move.
                String rootUsername = userService.authDAO.getAuth(rootAuth).username();
                String whiteUsername = gameService.getGame(cmd.getGameID()).whiteUsername();
                String blackUsername = gameService.getGame(cmd.getGameID()).blackUsername();
                if (!game.validMoves(move.getStartPosition()).contains(move) ||//invalid move
                        (game.getTeamTurn() == ChessGame.TeamColor.WHITE && !rootUsername.equals(whiteUsername)) ||//black moves on white turn
                        (game.getTeamTurn() == ChessGame.TeamColor.BLACK && !rootUsername.equals(blackUsername))) {//white moves on black turn
                    sendError("Move not valid", ctx);
                    break;
                }

                //Game is updated to represent the move. Game is updated in the database.
                game.makeMove(move);
                updateGame(game, gameService, cmd.getGameID());
                //Server sends a LOAD_GAME message to all clients in the game (including
                // the root client) with an updated game.
                loadEveryonesGame(cmd.getGameID());

                //Server sends a Notification message to all other clients in that game informing them what move was made.
                String user = cmd.getUser();
                String moveString;
                if (move.getPromotionPiece() != null) {
                    moveString = move.getStartPosition() + " to " + move.getEndPosition();
                } else {
                    moveString = move.toString();
                }
                notifyEveryoneElse("User " + user + " made move " + moveString, cmd.getGameID(), ctx);

                //If the move results in check, checkmate or stalemate the server sends a
                // Notification message to all clients.
                //notify if a move results in check, checkmate, or stalemate

                //will affect the new user's turn, not the current user
                ChessGame.TeamColor color = game.getTeamTurn();
                if (game.isInCheckmate(color)) {
                    game.endGame();
                    updateGame(game, gameService, cmd.getGameID());
                    ChessGame.TeamColor checkmatee;
                    if (color == ChessGame.TeamColor.WHITE) {
                        checkmatee = ChessGame.TeamColor.BLACK;
                    } else {
                        checkmatee = ChessGame.TeamColor.WHITE;
                    }
                    String message = checkmatee + " has been checkmated!";
                    notifyEveryone(message, cmd.getGameID());
                } else if (game.isInStalemate(color)) {
                    game.endGame();
                    updateGame(game, gameService, cmd.getGameID());
                    String message = "the game is a stalemate!";
                    notifyEveryone(message, cmd.getGameID());
                } else if (game.isInCheck(color)) {
                    ChessGame.TeamColor checkee;
                    if (color == ChessGame.TeamColor.WHITE) {
                        checkee = ChessGame.TeamColor.BLACK;
                    } else {
                        checkee = ChessGame.TeamColor.WHITE;
                    }
                    String message = checkee + " is in check!";
                    notifyEveryone(message, cmd.getGameID());
                }


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
    }

    ///helper functions for handling

    private void loadMyGame(int gameID, WsContext ctx) {
        ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        ChessGame game = gameService.getGame(gameID).game();
        msg.setGame(game);
        ctx.send(gson.toJson(msg));
//                System.out.println("LOAD_GAME sent back");
    }

    private void loadEveryonesGame(int gameID) {
        ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        ChessGame game = gameService.getGame(gameID).game();
        msg.setGame(game);
        for (WsContext context : gameSessions.get(gameID)) {//to everyone
            context.send(gson.toJson(msg));
//            System.out.println("LOAD_GAME sent back");
        }
    }

    private void updateGame(ChessGame updatedGame, GameService gameService, int gameID) {
        GameData oldGameData = gameService.getGame(gameID);
        GameData newGameData = new GameData(oldGameData.gameID(), oldGameData.whiteUsername(), oldGameData.blackUsername(), oldGameData.gameName(), updatedGame);
        gameService.gameDAO.updateGame(newGameData);

    }

    //getUserColor(gameService.getGame(cmd.getGameID()), cmd.getUser());
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

    void sendError(String errorMessage, WsContext context) {
        ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        msg.setErrorMessage(errorMessage);
        context.send(gson.toJson(msg));
    }

    void notifyEveryone(String message, int gameID) {
        for (WsContext context : gameSessions.get(gameID)) {//to everyone
            ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            msg.setMessage(message);
            context.send(gson.toJson(msg));
//            System.out.println("NOTIFICATION sent back");
        }
    }

    void notifyEveryoneElse(String message, int gameID, WsContext currentContext) {
        for (WsContext context : gameSessions.get(gameID)) {
            if (!context.sessionId().equals(currentContext.sessionId())) {//send it to other clients, not the root client
                ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                msg.setMessage(message);
                context.send(gson.toJson(msg));
//                        System.out.println("NOTIFICATION sent back");
            }
        }
    }
}
