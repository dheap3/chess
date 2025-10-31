package dataaccess;

import chess.ChessGame;
import datamodel.AuthData;
import datamodel.GameData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    SELECT * FROM gameData WHERE gameID = ?;
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        //gameID already there
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        String gameJSON = rs.getString("gameJSON");
                        var serializer = new Gson();
                        ChessGame game = serializer.fromJson(gameJSON, ChessGame.class);
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public GameData updateGame(GameData newGame) {
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    UPDATE gameData SET whiteUsername = ?, blackUsername = ?, gameJson = ? WHERE gameID = ?;
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                Gson gson = new Gson();
                String gameJSON = gson.toJson(newGame.game());
                preparedStatement.setString(1, newGame.whiteUsername());
                preparedStatement.setString(2, newGame.blackUsername());
                preparedStatement.setString(3, gameJSON);
                preparedStatement.setInt(4, newGame.gameID());

                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return newGame;
    }

    @Override
    public Collection<GameData> getGames() {
        ArrayList<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    SELECT * FROM gameData;
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        int gameID = rs.getInt("gameID");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        String gameJSON = rs.getString("gameJSON");
                        var serializer = new Gson();
                        ChessGame game = serializer.fromJson(gameJSON, ChessGame.class);
                        GameData currGameData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                        games.add(currGameData);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return games;
    }

    @Override
    public void clearDB() {
        try (var conn = DatabaseManager.getConnection()) {
            String sqlComm = """
                    TRUNCATE TABLE gameData;
                    """;

            try (var preparedStatement = conn.prepareStatement(sqlComm)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
