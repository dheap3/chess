package dataaccess;

import datamodel.GameData;

public interface GameDAO {
    boolean addGame(GameData game);
    GameData getGame(int gameID);
    void clearDB();
}
