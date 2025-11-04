package dataaccess;

import datamodel.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private Map<Integer, GameData> games = new HashMap<Integer, GameData>();

    @Override
    public boolean addGame(GameData game) {
        if (game == null) {
            return false;
        }
        Integer gameID = game.gameID();
        games.put(gameID, game);
        return true;
    }

    @Override
    public GameData getGame(Integer gameID) {
        return games.get(gameID);
    }

    @Override
    public GameData updateGame(GameData newGame) {
        games.replace(newGame.gameID(), newGame);
        return newGame;
    }

    @Override
    public ArrayList<GameData> getGames() {
        ArrayList<GameData> gamesList = new ArrayList<>(games.values());
        return gamesList;
    }

    @Override
    public void clearDB() {
        games.clear();
    }
}
