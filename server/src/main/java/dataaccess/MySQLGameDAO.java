package dataaccess;

import datamodel.GameData;
import com.google.gson.Gson;

import java.util.Collection;
import java.util.List;

public class MySQLGameDAO implements GameDAO {

    public MySQLGameDAO() throws Exception {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    CREATE TABLE IF NOT EXISTS gameData (
                        gameID INT PRIMARY KEY,
                        whiteUsername VARCHAR(255),
                        blackUsername VARCHAR(255),
                        gameName VARCHAR(255),
                        gameJSON TEXT
                    );
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                //this statement returns the number of rows affected, we currently don't use that rn
                preparedStatement.executeUpdate();
            }
        }
    }

    @Override
    public boolean addGame(GameData game) {
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    INSERT INTO gameData (gameID, whiteUsername, blackUsername, gameName, gameJSON) VALUES (?, ?, ?, ?, ?);
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                preparedStatement.setInt(1, game.gameID());
                preparedStatement.setString(2, game.whiteUsername());
                preparedStatement.setString(3, game.blackUsername());
                preparedStatement.setString(4, game.gameName());
                Gson gson = new Gson();
                String gameJSON = gson.toJson(game.game());
                preparedStatement.setString(5, gameJSON);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
