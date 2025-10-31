package dataaccess;

import datamodel.GameData;

import java.util.Collection;
import java.util.List;

public class MySQLGameDAO implements GameDAO {
    @Override
    public boolean addGame(GameData game) {
        return false;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public GameData updateGame(GameData newGame) {
        return null;
    }

    @Override
    public Collection<GameData> getGames() {
        return List.of();
    }

    @Override
    public void clearDB() {

    }
}
