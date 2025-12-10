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
            System.out.println("close");
        });

        ws.onError(ctx -> {
            ctx.error().printStackTrace();
            System.out.println(ctx.error().getMessage());
//            System.out.println("error");
        });
    }

    private void handle(WsMessageContext ctx, UserGameCommand cmd) throws Exception{
        switch (cmd.getCommandType()) {
            case CONNECT -> {
                int gameID = cmd.getGameID();
                String auth = cmd.getAuthToken();
                if (gameService.getGame(gameID) == null) {
                    sendError("Game not found", ctx); break; }
                if (userService.authDAO.getAuth(auth) == null) {
                    sendError("You are not authorized to access this game", ctx); break;
                }
                gameSessions.putIfAbsent(gameID, new ArrayList<>());
                if (!gameSessions.get(gameID).contains(ctx)) {
                    gameSessions.get(gameID).add(ctx);//add the session to the gameSessions
                }
                loadMyGame(cmd.getGameID(), ctx);
                String user = cmd.getUser();
                String type = cmd.getUserType();
                notifyEveryoneElse(user + " connected (" + type + ")", cmd.getGameID(), ctx);
            }
            case MAKE_MOVE -> {
                ChessMove move = cmd.getMove();
                ChessGame game = gameService.getGame(cmd.getGameID()).game();
                if (game.isOver()) {//checks if the game can be updated (if the state of the game isOver)
                    String message = "Game already over";
                    sendError(message, ctx); break;
                }
                String rootAuth = cmd.getAuthToken();//Checks if the user sending the command is authorized
                if (!userService.authDAO.dbContains(rootAuth)) {//rootClient's auth is in the db
                    sendError("You are not authorized", ctx); break;
                }
                //Server verifies the validity of the move.
                String rootUsername = userService.authDAO.getAuth(rootAuth).username();
                String whiteUsername = gameService.getGame(cmd.getGameID()).whiteUsername();
                String blackUsername = gameService.getGame(cmd.getGameID()).blackUsername();
                //need to check if observer can make a move
                if (game.validMoves(move.getStartPosition()) == null ||
                        !game.validMoves(move.getStartPosition()).contains(move) ||//invalid move
                        (game.getTeamTurn() == ChessGame.TeamColor.WHITE && !rootUsername.equals(whiteUsername)) ||//black moves on white turn
                        (game.getTeamTurn() == ChessGame.TeamColor.BLACK && !rootUsername.equals(blackUsername))) {//white moves on black turn
                    sendError("Move not valid", ctx); break;
                }
                game.makeMove(move);
                updateChessGame(game, gameService, cmd.getGameID());
                loadEveryonesGame(cmd.getGameID());

                String user = cmd.getUser();
                String moveString;
                if (move.getPromotionPiece() == null) {
                    moveString = move.getStartPosition() + " to " + move.getEndPosition();
                } else {
                    moveString = move.toString();
                }
                notifyEveryoneElse(user + " made move " + moveString, cmd.getGameID(), ctx);
                ChessGame.TeamColor color = game.getTeamTurn();//will affect the new user's turn, not the current user
                if (game.isInCheckmate(color)) {
                    game.endGame();
                    updateChessGame(game, gameService, cmd.getGameID());
//                    ChessGame.TeamColor checkmatee;
//                    if (color == ChessGame.TeamColor.WHITE) { checkmatee = ChessGame.TeamColor.BLACK;
//                    } else { checkmatee = ChessGame.TeamColor.WHITE; }
                    String message = color + " has been checkmated!";
                    notifyEveryone(message, cmd.getGameID());
                } else if (game.isInStalemate(color)) {
                    game.endGame();
                    updateChessGame(game, gameService, cmd.getGameID());
                    String message = "the game is a stalemate!";
                    notifyEveryone(message, cmd.getGameID());
                } else if (game.isInCheck(color)) {
                    ChessGame.TeamColor checkee;
                    if (color == ChessGame.TeamColor.WHITE) { checkee = ChessGame.TeamColor.BLACK;
                    } else { checkee = ChessGame.TeamColor.WHITE; }
                    String message = checkee + " is in check!";
                    notifyEveryone(message, cmd.getGameID());
                }
            }
            case LEAVE -> {
                ChessGame game = gameService.getGame(cmd.getGameID()).game();
                leaveGameSession(cmd.getGameID(), ctx);
                updateUser(null, game.getTeamTurn(), gameService, cmd.getGameID());
                notifyEveryoneElse(game.getTeamTurn() + " left the game!", cmd.getGameID(), ctx);
            }
            case RESIGN -> {
                ChessGame game = gameService.getGame(cmd.getGameID()).game();
                if (game.isOver()) {
                    sendError("Game is already over. Can't resign.", ctx); break;
                }
                String rootAuth = cmd.getAuthToken();
                String rootUsername = userService.authDAO.getAuth(rootAuth).username();
                String whiteUsername = gameService.getGame(cmd.getGameID()).whiteUsername();
                String blackUsername = gameService.getGame(cmd.getGameID()).blackUsername();
                if (!rootUsername.equals(whiteUsername) && !rootUsername.equals(blackUsername)) {
                    sendError("You are observing not playing", ctx); break; }
                game.endGame();
                updateChessGame(game, gameService, cmd.getGameID());
                ChessGame.TeamColor color = game.getTeamTurn();
                notifyEveryone(color + " resigned! Game is over.", cmd.getGameID());
                updateChessGame(game, gameService, cmd.getGameID());
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

    private void updateChessGame(ChessGame updatedGame, GameService gameService, int gameID) {
        GameData oldGameData = gameService.getGame(gameID);
        GameData newGameData = new GameData(oldGameData.gameID(), oldGameData.whiteUsername(),
                oldGameData.blackUsername(), oldGameData.gameName(), updatedGame);
        gameService.gameDAO.updateGame(newGameData);

    }

    void sendError(String errorMessage, WsContext context) {
        ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        msg.setErrorMessage("\n" + errorMessage);
        context.send(gson.toJson(msg));
    }

    void notifyEveryone(String message, int gameID) {
        for (WsContext context : gameSessions.get(gameID)) {//to everyone
            ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            msg.setMessage("\n" + message);
            context.send(gson.toJson(msg));
//            System.out.println("NOTIFICATION sent back");
        }
    }

    void notifyEveryoneElse(String message, int gameID, WsContext currentContext) {
        for (WsContext context : gameSessions.get(gameID)) {
            if (!context.sessionId().equals(currentContext.sessionId())) {//send it to other clients, not the root client
                ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                msg.setMessage("\n" + message);
                context.send(gson.toJson(msg));
//                        System.out.println("NOTIFICATION sent back");
            }
        }
    }

    private void updateUser(String newUsername, ChessGame.TeamColor userColor, GameService gameService, int gameID) {
        GameData oldGameData = gameService.getGame(gameID);
        GameData newGameData;
        if (userColor.equals(ChessGame.TeamColor.WHITE)) {
            newGameData = new GameData(oldGameData.gameID(), newUsername, oldGameData.blackUsername(), oldGameData.gameName(), oldGameData.game());
        } else {
            newGameData = new GameData(oldGameData.gameID(), oldGameData.whiteUsername(), newUsername, oldGameData.gameName(), oldGameData.game());
        }
        gameService.gameDAO.updateGame(newGameData);

    }

    private void leaveGameSession(int gameID, WsMessageContext currentContext) {
        for (WsContext context : gameSessions.get(gameID)) {
            if (context.sessionId().equals(currentContext.sessionId())) {
                gameSessions.get(gameID).remove(context);
                if (gameSessions.get(gameID).isEmpty()) {
                    gameSessions.remove(gameID);
                }
                break;
            }
        }
    }
}
