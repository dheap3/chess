package datamodel;

import java.util.ArrayList;

public class ListGamesResponse {
    ArrayList<GameData> games;//ignores the ChessGame game value in GameData and leaves it
    ListGamesResponse(ArrayList<GameData> games) {
        this.games = games;
    }
}
