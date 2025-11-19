package datamodel;

import java.util.ArrayList;

public class ListGamesResponse {
    ArrayList<GameData> games;//ignores the ChessGame game value in GameData and leaves it
    ListGamesResponse(ArrayList<GameData> games) {
        this.games = games;
    }

    public ArrayList<GameData> getGames() {
        return games;
    }

    @Override
    public String toString() {
        String response = "";
        for (GameData game : games) {
            response += (game.gameID() + " = gameID and gameName = " + game.gameName());
        }
        return response;
    }
}
