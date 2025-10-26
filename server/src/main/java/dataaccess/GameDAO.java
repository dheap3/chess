package dataaccess;

import datamodel.GameData;

public interface GameDAO {
    boolean addGame(GameData game);
    boolean getGame(int gameID);
}
