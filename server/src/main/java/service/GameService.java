package service;

import chess.ChessGame;
import datamodel.GameData;

import java.util.ArrayList;

public class GameService {
    public int numGames = 1;
    public ArrayList<GameData> listGames() {
        ArrayList<GameData> games = new ArrayList<>();
        //fill in rest of logic here

        return games;
    }
    public GameData createGame(String gameName) {
        int gameID = numGames++;
        //customize?
        String whiteUsername = null;
        String blackUsername = null;
        ChessGame game = new ChessGame();
        GameData gamePackage = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        return gamePackage;
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
}
