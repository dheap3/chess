package dataaccess;

import datamodel.GameData;

import java.util.Collection;

public interface GameDAO {
    boolean addGame(GameData game);
    GameData getGame(Integer gameID);
    GameData updateGame(GameData newGame);
    Collection<GameData> getGames();
    void clearDB();
}
