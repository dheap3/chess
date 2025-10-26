package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import datamodel.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameService {
    GameDAO gameDAO = new MemoryGameDAO();
    private AuthDAO authDAO = new MemoryAuthDAO();

    public GameService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }
    public int numGames = 1;

    public ArrayList<GameData> listGames() {
        ArrayList<GameData> games = new ArrayList<>();
        //fill in rest of logic here

        return games;
    }
    public Map<Integer, Map<String, String>> createGame(String authToken, String gameName) {
        Map<String, String> statusString = new HashMap<>();
        int statusCode;

        int gameID = numGames++;
        String whiteUsername = null;
        String blackUsername = null;
        ChessGame game = new ChessGame();
        GameData gamePackage = new GameData(gameID, whiteUsername, blackUsername, gameName, game);

        //checks if the user sent valid data
        if (gameName == null || gameName.isBlank()) {
            statusString = Map.of("message", "Error: bad request");
            statusCode = 400;
            return Map.of(statusCode, statusString);
        }

        //check if the authToken exists in db
        if (!authDAO.dbContains(authToken)) {
            statusString = Map.of("message", "Error: unauthorized");
            statusCode = 401;
            return Map.of(statusCode, statusString);
        }

        GameData myGamePackage = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        gameDAO.addGame(myGamePackage);

        //success
        statusString = Map.of("gameID", String.valueOf(gameID));
        statusCode = 200;
        return Map.of(statusCode, statusString);
    }
    public GameData joinGame(String user, String color, GameData game) {
        //because GameData is a record, we are going to make a copy with the username updated
        //if the game doesn't have users yet it won't take much to copy
        if (color.equals("WHITE")) {
            GameData gameWithUser = new GameData(game.gameID(), user, game.blackUsername(), game.gameName(), game.game());
            return gameWithUser;
        } else {
            GameData gameWithUser = new GameData(game.gameID(), game.whiteUsername(), user, game.gameName(), game.game());
            return gameWithUser;
        }

    }
    public GameData getGame(int gameID) {
        return gameDAO.getGame(gameID);
    }
    public void clearDB() {
        gameDAO.clearDB();
    }
}
