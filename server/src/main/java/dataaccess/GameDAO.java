package dataaccess;

import datamodel.GameData;

import java.util.ArrayList;
import java.util.Collection;

public interface GameDAO {
    boolean addGame(GameData game);
    GameData getGame(int gameID);
    GameData updateGame(GameData newGame);
    Collection<GameData> getGames();
    void clearDB();
}
