package dataaccess;

import datamodel.GameData;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private Map<Integer, GameData> games = new HashMap<Integer, GameData>();

    @Override
    public boolean addGame(GameData game) {
        Integer gameID = game.gameID();
        games.put(gameID, game);
        return true;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public void clearDB() {
        games.clear();
    }
}
