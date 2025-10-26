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

    public Map<Integer, String> listGames(String authToken) {
        Map<String, String> statusString = new HashMap<>();
        int statusCode;
        ArrayList<GameData> games = new ArrayList<>();

        //check if the authToken exists in db
        if (!authDAO.dbContains(authToken)) {
            statusString = Map.of("message", "Error: unauthorized");
            statusCode = 401;
            return Map.of(statusCode, new Gson().toJson(statusString));
        }

        ArrayList<GameData> gamesList = new ArrayList<>(gameDAO.getGames());
        var res = new Gson().toJson(Map.of("games", gamesList));
        statusCode = 200;
        return Map.of(statusCode, res);
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
    public Map<Integer, Map<String, String>> joinGame(String authToken, String playerColor, Double gameIDDouble) {
        Map<String, String> statusString = new HashMap<>();
        int statusCode;

        //checks if the user sent valid data
        if (playerColor == null || playerColor.isBlank() ||
                (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) ||
                gameIDDouble == null) {
            statusString = Map.of("message", "Error: bad request");
            statusCode = 400;
            return Map.of(statusCode, statusString);
        }
        //nasty double conversion
        double dub = gameIDDouble;
        int gameID = (int) dub;

        //check if the authToken exists in db
        if (!authDAO.dbContains(authToken)) {
            statusString = Map.of("message", "Error: unauthorized");
            statusCode = 401;
            return Map.of(statusCode, statusString);
        }
        String user = authDAO.getAuth(authToken).username();

        GameData gamePackage = getGame(gameID);
        //verify that the game is accepting a user of the color given ie: not full
        if ((playerColor.equals("WHITE") && gamePackage.whiteUsername() != null) ||
                (playerColor.equals("BLACK") && gamePackage.blackUsername() != null)) {
            statusString = Map.of("message", "Error: already taken");
            statusCode = 403;
            return Map.of(statusCode, statusString);
        }

        //because GameData is a record, we are going to make a copy with the username updated
        //if the game doesn't have users yet it won't take much to copy
        if (playerColor.equals("WHITE")) {
            GameData gameWithUser = new GameData(gamePackage.gameID(), user, gamePackage.blackUsername(), gamePackage.gameName(), gamePackage.game());
            gameDAO.updateGame(gameWithUser);
        } else {
            GameData gameWithUser = new GameData(gamePackage.gameID(), gamePackage.whiteUsername(), user, gamePackage.gameName(), gamePackage.game());
            gameDAO.updateGame(gameWithUser);
        }

        //success
        statusString = Map.of("gameID", String.valueOf(gameID));
        statusCode = 200;
        return Map.of(statusCode, statusString);

    }
    public GameData getGame(int gameID) {
        return gameDAO.getGame(gameID);
    }
    public void clearDB() {
        gameDAO.clearDB();
    }
}
