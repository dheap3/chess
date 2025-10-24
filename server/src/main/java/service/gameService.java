package service;

import chess.ChessGame;
import dataModel.GameData;

import java.util.ArrayList;
import java.util.Map;

public class gameService {
    public int numGames = 1;
    public ArrayList<GameData> listGames() {
        ArrayList<GameData> games = new ArrayList<>();
        //fill in rest of logic here

        return games;
    }
    public GameData createGame(String gameName) {
        int gameID = numGames++;
        //customize?
        String whiteUsername = "whiteUser";
        String blackUsername = "blackUser";
        ChessGame game = new ChessGame();
        GameData gamePackage = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        return gamePackage;
    }
}
